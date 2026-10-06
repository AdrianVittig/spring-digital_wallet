package com.vittig.spring_digital_wallet.dto.wallet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopUpWalletDto {
    @Positive
    @NotNull
    private BigDecimal amount;
}
