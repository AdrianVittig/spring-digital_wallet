package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.dto.wallet.WalletDto;
import com.vittig.spring_digital_wallet.dto.wallet.WalletTopUpDto;
import com.vittig.spring_digital_wallet.service.contract.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public WalletDto getCurrentWallet(){
        return this.walletService.getCurrentWallet();
    }

    @PatchMapping("/top-up")
    public WalletDto topUpWallet(@RequestBody WalletTopUpDto walletTopUpDto){
        return this.walletService.topUpWallet(walletTopUpDto.getAmount());
    }
}
