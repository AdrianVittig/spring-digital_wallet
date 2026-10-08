package com.vittig.spring_digital_wallet.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transfer extends BaseEntity{
    private String fromIban;
    private String toIban;

    private BigDecimal amount;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "transfer")
    private List<WalletEntry> entries;
}
