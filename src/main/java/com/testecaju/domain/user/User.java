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
    @CollectionTable(name = "user_wallet", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "mcc")
    @Column(name = "amount")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<MCCType, BigDecimal> wallet = new HashMap<>();

    public User(String id, String name, HashMap<MCCType, BigDecimal> wallet) {
        super(id, name);
        this.wallet = wallet;
    }

    public void updateWallet(MCCType mcc, BigDecimal value){
        wallet.put(mcc, value);
    }
}
