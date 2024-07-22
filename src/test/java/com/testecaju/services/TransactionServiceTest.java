package com.testecaju.services;

import com.testecaju.domain.user.MCCType;
import com.testecaju.domain.user.Merchant;
import com.testecaju.domain.user.User;
import com.testecaju.dtos.TransactionDTO;
import com.testecaju.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    MerchantService merchantService;
    @Mock
    UserService userService;

    @Autowired
    @InjectMocks
    TransactionService transactionService;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("Authorize a transaction successfully without fallback using the cash balance.")
    void authorizeCase1() throws Exception {
        String userId = UUID.randomUUID().toString();
        String merchantId = UUID.randomUUID().toString();
        String merchantName = "PAG*JoseDaSilva          RIO DE JANEI BR";
        String merchantMcc = "5411";

        User user = new User(userId, "Maria", new HashMap<>() {{
            put(MCCType.MEAL, new BigDecimal(100));
            put(MCCType.CASH, new BigDecimal(50));
            put(MCCType.FOOD, new BigDecimal(150));
        }});
        Merchant merchant = new Merchant(merchantId, merchantName, MCCType.FOOD, new BigDecimal(150));
        when(userService.findUserById(userId)).thenReturn(user);
        when(merchantService.findMerchantByNameIgnoreCase(merchantName)).thenReturn(merchant);

        TransactionDTO request = new TransactionDTO(userId, new BigDecimal(100), merchantMcc, merchantName);
        String result = transactionService.authorize(request);

        verify(transactionRepository, times(1)).save(any());
        verify(userService, times(1)).saveUser(user);
        verify(merchantService, times(1)).saveMerchant(merchant);

        assertThat(result.equals("00")).isTrue();
    }

    @Test
    @DisplayName("Authorize a transaction successfully with fallback, using the cash balance, and passing the wrong MCC.")
    void authorizeCase2() throws Exception {
        String userId = UUID.randomUUID().toString();
        String merchantId = UUID.randomUUID().toString();
        String merchantName = "UBER EATS                   SAO PAULO BR";
        String merchantMcc = "5400"; // Set random MCC to interpret it as CASH, although it should be MEAL

        User user = new User(userId, "Maria", new HashMap<>() {{
            put(MCCType.MEAL, new BigDecimal(100));
            put(MCCType.CASH, new BigDecimal(150));
            put(MCCType.FOOD, new BigDecimal(150));
        }});
        Merchant merchant = new Merchant(merchantId, merchantName, MCCType.MEAL, new BigDecimal(150));
        when(userService.findUserById(userId)).thenReturn(user);
        when(merchantService.findMerchantByNameIgnoreCase(merchantName)).thenReturn(merchant);

        TransactionDTO request = new TransactionDTO(userId, new BigDecimal(150), merchantMcc, merchantName);
        String result = transactionService.authorize(request);

        verify(transactionRepository, times(1)).save(any());
        verify(userService, times(1)).saveUser(user);
        verify(merchantService, times(1)).saveMerchant(merchant);

        assertThat(result.equals("00")).isTrue();
    }

    @Test
    @DisplayName("Reject a transaction with not enough balance in the user wallet.")
    void authorizeCase3() throws Exception {
        String userId = UUID.randomUUID().toString();
        String merchantId = UUID.randomUUID().toString();
        String merchantName = "UBER EATS                   SAO PAULO BR";
        String merchantMcc = "5400"; // Set random MCC to interpret it as CASH, although it should be MEAL

        User user = new User(userId, "Maria", new HashMap<>() {{
            put(MCCType.MEAL, new BigDecimal(100));
            put(MCCType.CASH, new BigDecimal(50));
            put(MCCType.FOOD, new BigDecimal(150));
        }});
        Merchant merchant = new Merchant(merchantId, merchantName, MCCType.MEAL, new BigDecimal(150));
        when(userService.findUserById(userId)).thenReturn(user);
        when(merchantService.findMerchantByNameIgnoreCase(merchantName)).thenReturn(merchant);

        TransactionDTO request = new TransactionDTO(userId, new BigDecimal(250), merchantMcc, merchantName);
        String result = transactionService.authorize(request);

        verify(transactionRepository, times(0)).save(any());
        verify(userService, times(0)).saveUser(user);
        verify(merchantService, times(0)).saveMerchant(merchant);

        assertThat(result.equals("51")).isTrue();
    }

    @Test
    @DisplayName("Reject a transaction for any other reason.")
    void authorizeCase4() throws Exception {
        String userId = UUID.randomUUID().toString();

        when(userService.findUserById(userId)).thenThrow(new Exception("Usuário não encontrado."));

        TransactionDTO request = new TransactionDTO(userId, new BigDecimal(250), "5411", "PICPAY*BILHETEUNICO           GOIANIA BR");
        String result = transactionService.authorize(request);

        assertThat(result.equals("07")).isTrue();
    }
}