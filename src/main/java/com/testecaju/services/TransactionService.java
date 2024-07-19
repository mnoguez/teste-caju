package com.testecaju.services;

import com.testecaju.domain.transaction.Transaction;
import com.testecaju.domain.user.MCCType;
import com.testecaju.domain.user.Merchant;
import com.testecaju.domain.user.User;
import com.testecaju.dtos.TransactionDTO;
import com.testecaju.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private static final Map<String, String> merchantNames = new HashMap<>(){{
        put("eat", "5811");
        put("padaria", "5411");
        put("super", "5411");
        put("mercado", "5411");
        put("restaurante", "5811");
    }};

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    MerchantService merchantService;
    @Autowired
    UserService userService;

    @Transactional
    private void finishTransaction(User user, MCCType mcc, Merchant merchant, TransactionDTO transactionPayload, Boolean isFallback) throws Exception{
        if (!isFallback){
            // Simple authorizer
            BigDecimal newBalance = user.getWallet().get(mcc).subtract(transactionPayload.totalAmount());
            user.updateWallet(mcc, newBalance);
        }
        else{
            // Fallback authorizer
            BigDecimal currentMccBalance = user.getWallet().get(mcc);
            BigDecimal diffBalance = transactionPayload.totalAmount().subtract(currentMccBalance);
            BigDecimal newCashBalance = user.getWallet().get(MCCType.CASH).subtract(diffBalance);
            user.updateWallet(mcc, new BigDecimal(0));
            user.updateWallet(MCCType.CASH, newCashBalance);

        }
        logger.info("Authorizing transaction and updating wallets");

        // Update merchant wallet
        merchant.setWallet(merchant.getWallet().add(transactionPayload.totalAmount()));

        // Create new transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transactionPayload.totalAmount());
        newTransaction.setUser(user);
        newTransaction.setMcc(mcc);
        newTransaction.setMerchant(merchant);

        // Save updated entities in the database
        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(user);
        this.merchantService.saveMerchant(merchant);

        logger.info("Authorized transaction");
    }

    private MCCType searchMCC(String merchantName, String mcc){
        String merchantLowerCase = merchantName.toLowerCase();

        for (Map.Entry<String, String> entry : merchantNames.entrySet()){
            if (merchantLowerCase.contains(entry.getKey().toLowerCase())){
                logger.info("MCC found in merchant name mapping, using MCC {} to verify balance", entry.getValue());

                return this.getMCC(entry.getValue());
            }
        }

        return this.getMCC(mcc);
    }

    private MCCType getMCC(String mcc){
        logger.info("Getting MCC type for MCC {}", mcc);

        if (mcc.equals("5411") || mcc.equals("5412"))
            return MCCType.FOOD;
        else if (mcc.equals("5811") || mcc.equals("5812"))
            return MCCType.MEAL;

        return MCCType.CASH;
    }

    public String authorize(TransactionDTO transactionPayload) {
        try {
            logger.info("Trying to get user {} and merchant {}, {}", transactionPayload.account(), transactionPayload.merchant(), transactionPayload.mcc());
            User user = this.userService.findUserById(transactionPayload.account());
            Merchant merchant = this.merchantService.findMerchantByNameIgnoreCase(transactionPayload.merchant());

            // Get the transaction MCC type
            MCCType transactionMCC = this.searchMCC(transactionPayload.merchant(), transactionPayload.mcc());

            // Verify if the wallet contains the balance to discount
            BigDecimal currentBalance = user.getWallet().getOrDefault(transactionMCC, BigDecimal.ZERO);
            if (currentBalance.compareTo(transactionPayload.totalAmount()) >= 0){
                this.finishTransaction(user, transactionMCC, merchant, transactionPayload, false);
                return "00";
            }
            // Verify fallback cash wallet has enough remaining balance
            else if (transactionMCC.equals(MCCType.FOOD) || transactionMCC.equals(MCCType.MEAL)){
                BigDecimal cashBalance = user.getWallet().getOrDefault(MCCType.CASH, BigDecimal.ZERO);

                if (currentBalance.add(cashBalance).compareTo(transactionPayload.totalAmount()) >= 0){
                    this.finishTransaction(user, transactionMCC, merchant, transactionPayload, true);
                    return "00";
                }
                else{
                    logger.info("Unauthorized, user with insufficient CASH and {} balance", transactionMCC);
                    return "51";
                }
            }
            // Reject operation, insufficient balance
            else {
                logger.info("Unauthorized, user with insufficient balance for wallet {}", transactionMCC);
                return "51";
            }
        } catch (Exception e) {
            logger.error("Error updating balance for mcc {} in user account {}", transactionPayload.mcc(), transactionPayload.account(), e);
        }

        return "07";
    }
}
