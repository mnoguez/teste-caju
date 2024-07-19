package com.testecaju.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity(name="users")
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Account {
    @ElementCollection
    @MapKeyColumn
    @CollectionTable
    private Map<MCCType, BigDecimal> wallet;

    public User(String id, String name, HashMap<MCCType, BigDecimal> wallet) {
        super(id, name);
        this.wallet = wallet;
    }

    public void updateWallet(MCCType mcc, BigDecimal value){
        wallet.put(mcc, value);
    }
}
