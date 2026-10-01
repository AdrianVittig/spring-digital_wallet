package com.vittig.spring_digital_wallet.data.entity;

import com.vittig.spring_digital_wallet.data.util.MovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ledger_entries")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LedgerEntry extends BaseEntity{
    @ManyToOne
    private Wallet wallet;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private MovementType movementType;
    @ManyToOne
    private Transfer transfer;
}
