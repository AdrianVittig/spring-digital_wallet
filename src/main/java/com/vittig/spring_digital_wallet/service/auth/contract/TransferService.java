package com.vittig.spring_digital_wallet.service.auth.contract;

import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;

import java.math.BigDecimal;

public interface TransferService {
    TransferDto createTransfer(Long toWalletId, BigDecimal amount);
}
