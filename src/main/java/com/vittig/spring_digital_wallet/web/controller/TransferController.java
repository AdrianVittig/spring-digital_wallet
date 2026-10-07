package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferRequestDto;
import com.vittig.spring_digital_wallet.service.contract.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferService transferService;

    @GetMapping
    public List<TransferDto> getTransfersForCurrentUser(){
        return this.transferService.getTransfersForCurrentUser();
    }

    @PostMapping
    public TransferDto createTransfer(@Valid @RequestBody TransferRequestDto dto){
        return this.transferService.createTransfer(dto);
    }
}
