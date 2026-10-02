package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;

import java.math.BigDecimal;
import java.util.List;

public interface TransferService {
    List<TransferDto> getAllTransfers();
    TransferDto getTransferById(Long id);
    TransferDto createTransfer(Long toWalletId, BigDecimal amount);
}
