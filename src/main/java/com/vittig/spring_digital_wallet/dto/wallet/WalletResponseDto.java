package com.vittig.spring_digital_wallet.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDto {
    private Long id;
    private BigDecimal currentBalance;
    private String iban;
    private Long userId;
}
