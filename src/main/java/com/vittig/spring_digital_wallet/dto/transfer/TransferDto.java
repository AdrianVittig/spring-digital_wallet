package com.vittig.spring_digital_wallet.dto.transfer;

import com.vittig.spring_digital_wallet.data.util.TransferCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
    private Long id;
    private String fromIban;
    private String toIban;
    private BigDecimal amount;
    private TransferCategory category;
    private String description;
    private LocalDateTime createdAt;
}
