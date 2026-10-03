package com.vittig.spring_digital_wallet.dto.l_entryDto;

import com.vittig.spring_digital_wallet.data.util.MovementType;

import java.math.BigDecimal;

public class LedgerEntryDto {
    private Long walletId;
    private BigDecimal amount;
    private MovementType movementType;
    private Long transferId;
}
