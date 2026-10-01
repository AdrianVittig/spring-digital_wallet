package com.vittig.spring_digital_wallet.data.entity;

import com.vittig.spring_digital_wallet.data.util.TransferStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transfers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transfer extends BaseEntity{
    private BigDecimal amount;
    @ManyToOne
    private Wallet fromWallet;
    @ManyToOne
    private Wallet toWallet;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private TransferStatus status;
    @OneToMany(mappedBy = "transfer")
    private List<LedgerEntry> entryList;
}
