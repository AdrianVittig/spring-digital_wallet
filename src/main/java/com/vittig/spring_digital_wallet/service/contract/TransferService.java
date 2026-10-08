package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferFilterRequestDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferRequestDto;

import java.util.List;

public interface TransferService {
    List<TransferDto> getTransfersForCurrentUser(TransferFilterRequestDto dto);
    TransferDto createTransfer(TransferRequestDto dto);
}
