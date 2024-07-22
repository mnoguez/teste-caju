package com.testecaju.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "merchants")
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Merchant extends Account {
    @Enumerated(EnumType.STRING)
    private MCCType mcc;
    private BigDecimal wallet;

    public Merchant(String id, String name, MCCType mcc, BigDecimal balance) {
        super(id, name);
        this.mcc = mcc;
        this.wallet = balance;
    }
}
