package com.testecaju.services;

import com.testecaju.domain.user.Merchant;
import com.testecaju.repositories.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {
    @Autowired
    private MerchantRepository repository;

    public Merchant findMerchantByNameIgnoreCase(String name) throws Exception {
        return this.repository.findByNameIgnoreCase(name).orElseThrow(() -> new Exception("Lojista não encontrado."));
    }

    public void saveMerchant(Merchant merchant) {
        this.repository.save(merchant);
    }
}
