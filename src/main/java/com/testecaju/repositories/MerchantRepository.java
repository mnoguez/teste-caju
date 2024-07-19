package com.testecaju.repositories;

import com.testecaju.domain.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
    Optional<Merchant> findByNameIgnoreCase(String name);
}
