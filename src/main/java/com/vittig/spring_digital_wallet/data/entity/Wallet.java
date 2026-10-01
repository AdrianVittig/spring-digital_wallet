package com.vittig.spring_digital_wallet.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Wallet extends BaseEntity{
    @OneToOne(mappedBy = "wallet")
    private User owner;
    @Column(unique = true)
    private String iban;
    private BigDecimal balance;
}
