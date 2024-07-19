package com.testecaju.repositories;

import com.testecaju.domain.user.MCCType;
import com.testecaju.domain.user.Merchant;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MerchantRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(MerchantRepositoryTest.class);

    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Succesfully get the merchant from the database by its name, ignoring case.")
    void findByNameIgnoreCaseSuccess() {
        String merchantName = "PICPAY*BILHETEUNICO           GOIANIA BR";
        String merchantNameLower = merchantName.toLowerCase();
        Merchant createdMerchant = this.createMerchant(merchantName, MCCType.CASH, new BigDecimal(0));

        logger.info("Created merchant ({},{},{},{})", createdMerchant.getId(), createdMerchant.getName(), createdMerchant.getMcc(), createdMerchant.getWallet());

        Optional<Merchant> result = this.merchantRepository.findByNameIgnoreCase(merchantName);
        Optional<Merchant> resultLower = this.merchantRepository.findByNameIgnoreCase(merchantNameLower);

        // Assert that the original name is found
        assertThat(result.isPresent()).isTrue();
        // Assert that the lowercased name is also found, the function should be case insensitive
        assertThat(resultLower.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Do not get the merchant from the database.")
    void findByNameIgnoreCaseError() {
        String merchantName = "PICPAY*BILHETEUNICO           GOIANIA BR";

        Optional<Merchant> result = this.merchantRepository.findByNameIgnoreCase(merchantName);

        // Assert that the name is not found, as we do not create any merchant in the database
        assertThat(result.isEmpty()).isTrue();
    }

    private Merchant createMerchant(String name, MCCType mcc, BigDecimal balance){
        Merchant newMerchant = new Merchant();

        newMerchant.setName(name);
        newMerchant.setMcc(mcc);
        newMerchant.setWallet(balance);

        this.entityManager.persist(newMerchant);

        return newMerchant;
    }
}