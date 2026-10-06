package com.vittig.spring_digital_wallet.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends BaseEntity{
    private String iban;
    private BigDecimal currentBalance;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "wallet")
    private List<WalletEntry> entries;
}
