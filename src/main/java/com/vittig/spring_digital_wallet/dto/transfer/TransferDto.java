package com.vittig.spring_digital_wallet.dto.transfer;

import com.vittig.spring_digital_wallet.data.util.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
    private BigDecimal amount;
    private Long fromWalletId;
    private Long toWalletId;
    private LocalDateTime createdAt;
    private TransferStatus status;
    private List<Long> entryListIds;
}
