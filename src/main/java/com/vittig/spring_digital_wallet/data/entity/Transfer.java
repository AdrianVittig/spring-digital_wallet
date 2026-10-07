package com.vittig.spring_digital_wallet.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transfer extends BaseEntity{
    @OneToMany(mappedBy = "transfer")
    private List<WalletEntry> entries;

    private String toIban;

    private BigDecimal amount;
}
