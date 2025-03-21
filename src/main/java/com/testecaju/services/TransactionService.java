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
    private static final Map<String, String> merchantNames = new HashMap<>() {{
        put("eat", "5811");
        put("padaria", "5411");
        put("super", "5411");
        put("mercado", "5411");
        put("restaurante", "5811");
    }};

    @Autowired
    TransactionRepository repository;
    @Autowired
    MerchantService merchantService;
    @Autowired
    UserService userService;

    private Transaction createTransaction(User user, Merchant merchant, MCCType mcc, BigDecimal amount) {
        // Create new transaction
        Transaction newTransaction = new Transaction();

        newTransaction.setAmount(amount);
        newTransaction.setUser(user);
        newTransaction.setMcc(mcc);
        newTransaction.setMerchant(merchant);

        return newTransaction;
    }

    @Transactional
    private void saveTransaction(Transaction transaction, User user, Merchant merchant) {
        this.repository.save(transaction);
        this.userService.saveUser(user);
        this.merchantService.saveMerchant(merchant);
    }

    private void finishTransaction(User user, Merchant merchant, MCCType mcc, BigDecimal currentBalance, BigDecimal amount) {
        // Update user wallet
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.updateWallet(mcc, newBalance);

        // Update merchant wallet
        merchant.setWallet(merchant.getWallet().add(amount));

        // Create new transaction
        Transaction newTransaction = this.createTransaction(user, merchant, mcc, amount);

        // Save updated entities in the database
        this.saveTransaction(newTransaction, user, merchant);
    }

    private MCCType searchMCC(String merchantName, String mcc) {
        String merchantLowerCase = merchantName.toLowerCase();

        for (Map.Entry<String, String> entry : merchantNames.entrySet()) {
            if (merchantLowerCase.contains(entry.getKey().toLowerCase())) {
                logger.info("MCC found in merchant name mapping, using MCC {} to verify balance", entry.getValue());

                return this.getMCC(entry.getValue());
            }
        }

        return this.getMCC(mcc);
    }

    private MCCType getMCC(String mcc) {
        logger.info("Getting MCC type for MCC {}", mcc);

        if (mcc.equals("5411") || mcc.equals("5412"))
            return MCCType.FOOD;
        else if (mcc.equals("5811") || mcc.equals("5812"))
            return MCCType.MEAL;

        return MCCType.CASH;
    }

    private String authorizeTransaction(User user, Merchant merchant, MCCType mcc, BigDecimal balance, BigDecimal amount) {
        logger.info("Authorized transaction, finish updating wallets...");
        this.finishTransaction(user, merchant, mcc, balance, amount);
        logger.info("Finished transaction");

        return "00";
    }

    private String rejectTransaction(MCCType mcc) {
        logger.info("Unauthorized, user with insufficient balance for wallet {}", mcc);

        return "51";
    }

    private String fallbackAuthorizer(User user, Merchant merchant, BigDecimal amount) {
        BigDecimal cashBalance = user.getWallet().getOrDefault(MCCType.CASH, BigDecimal.ZERO);
        logger.info("Insufficient balance, verifying CASH: {}", cashBalance);

        if (cashBalance.compareTo(amount) >= 0) {
            return this.authorizeTransaction(user, merchant, MCCType.CASH, cashBalance, amount);
        } else {
            return this.rejectTransaction(MCCType.CASH);
        }
    }

    private String authorizer(User user, Merchant merchant, MCCType mcc, BigDecimal amount) {
        BigDecimal balance = user.getWallet().getOrDefault(mcc, BigDecimal.ZERO);
        logger.info("User wallet balance {} : {} : {}", user.getId(), mcc, balance);

        // Verify if the MCC wallet contains the balance to discount
        if (balance.compareTo(amount) >= 0) {
            return this.authorizeTransaction(user, merchant, mcc, balance, amount);
        }
        // Verify fallback cash wallet has enough remaining balance
        else if (mcc.equals(MCCType.FOOD) || mcc.equals(MCCType.MEAL)) {
            return this.fallbackAuthorizer(user, merchant, amount);
        }
        // Reject operation, insufficient balance
        else {
            return this.rejectTransaction(mcc);
        }
    }

    public String authorize(TransactionDTO transactionPayload) {
        try {
            // Get user and merchant
            logger.info("Trying to get user {} and merchant {}, {}", transactionPayload.account(), transactionPayload.merchant(), transactionPayload.mcc());
            User user = this.userService.findUserById(transactionPayload.account());
            Merchant merchant = this.merchantService.findMerchantByNameIgnoreCase(transactionPayload.merchant());

            // Get the transaction MCC type
            MCCType transactionMcc = this.searchMCC(transactionPayload.merchant(), transactionPayload.mcc());

            // Run authorizer
            return this.authorizer(user, merchant, transactionMcc, transactionPayload.totalAmount());
        } catch (Exception e) {
            logger.error("Error updating balance for mcc {} in user account {}", transactionPayload.mcc(), transactionPayload.account(), e);
        }

        return "07";
    }
}
