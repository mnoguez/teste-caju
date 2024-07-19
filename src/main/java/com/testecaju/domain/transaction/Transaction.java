package com.testecaju.domain.transaction;

import com.testecaju.domain.user.MCCType;
import com.testecaju.domain.user.Merchant;
import com.testecaju.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "transactions")
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private MCCType mcc;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
    private LocalDateTime timestamp;
}
