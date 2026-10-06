package com.vittig.spring_digital_wallet.data.entity;

import com.vittig.spring_digital_wallet.data.util.EntryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wallet_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletEntry extends BaseEntity{
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private EntryType entryType;

    @ManyToOne
    private Wallet wallet;

    @ManyToOne
    private Transfer transfer;
}
