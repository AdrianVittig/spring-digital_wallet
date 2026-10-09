package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.dto.page.PageResponseDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferFilterRequestDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferRequestDto;

import org.springframework.data.domain.Pageable;

public interface TransferService {
    PageResponseDto<TransferDto> getTransfersForCurrentUser(TransferFilterRequestDto dto, Pageable pageable);
    TransferDto createTransfer(TransferRequestDto dto);
}
