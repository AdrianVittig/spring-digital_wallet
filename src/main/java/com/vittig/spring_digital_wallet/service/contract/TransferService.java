package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.data.util.TransferStatus;
import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransferService {
    List<TransferDto> getAllTransfers();
    List<TransferDto> filterTransfers(BigDecimal minAmount, BigDecimal maxAmount,
                                      LocalDateTime startDate, LocalDateTime endDate,
                                      TransferStatus status);
    TransferDto getTransferById(Long id);
    TransferDto createTransfer(Long toWalletId, BigDecimal amount);
}
