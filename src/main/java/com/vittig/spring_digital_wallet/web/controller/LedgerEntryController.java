package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.dto.l_entryDto.LedgerEntryDto;
import com.vittig.spring_digital_wallet.service.contract.LedgerEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ledger")
public class LedgerEntryController {

    private final LedgerEntryService ledgerEntryService;

    @GetMapping
    public List<LedgerEntryDto> getCurrentWalletEntries(){
        return this.ledgerEntryService.getCurrentWalletEntries();
    }
}
