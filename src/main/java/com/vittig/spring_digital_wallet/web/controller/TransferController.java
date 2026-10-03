package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.data.util.TransferStatus;
import com.vittig.spring_digital_wallet.dto.l_entryDto.LedgerEntryDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferRequestDto;
import com.vittig.spring_digital_wallet.service.contract.LedgerEntryService;
import com.vittig.spring_digital_wallet.service.contract.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;
    private final LedgerEntryService ledgerEntryService;

    @GetMapping
    public List<TransferDto> getAllTransfers(){
        return this.transferService.getAllTransfers();
    }

    @GetMapping("/filter")
    public List<TransferDto> filter(@RequestParam(required = false) BigDecimal minAmount,
                                    @RequestParam(required = false) BigDecimal maxAmount,
                                    @RequestParam(required = false) LocalDateTime startDate,
                                    @RequestParam(required = false) LocalDateTime endDate,
                                    @RequestParam(required = false)TransferStatus status){
        return this.transferService.filterTransfers(minAmount, maxAmount, startDate, endDate, status);
    }

    @GetMapping("/{id}")
    public TransferDto getTransferById(@PathVariable Long id){
        return this.transferService.getTransferById(id);
    }

    @GetMapping("/{id}/ledger")
    public List<LedgerEntryDto> getEntriesByTransfer(@PathVariable Long id){
        return this.ledgerEntryService.getEntriesByTransferId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferDto createTransfer(@Valid @RequestBody TransferRequestDto dto){
        return this.transferService.createTransfer(dto.getToWalletId(), dto.getAmount());
    }

}
