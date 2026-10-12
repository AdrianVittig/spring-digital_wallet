package com.vittig.spring_digital_wallet.dto.transfer;

import com.vittig.spring_digital_wallet.data.util.TransferCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {
    @NotBlank
    private String toIban;
    @NotNull
    @Positive
    @Digits(integer = 36, fraction = 2)
    private BigDecimal amount;
    @NotNull
    private TransferCategory category;
    @Size(max = 255)
    private String description;
}
