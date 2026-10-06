package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.dto.wallet.TopUpWalletDto;
import com.vittig.spring_digital_wallet.dto.wallet.WalletResponseDto;
import com.vittig.spring_digital_wallet.service.contract.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletService walletService;

    @GetMapping
    public WalletResponseDto getCurrentWallet(){
        return this.walletService.getCurrentWallet();
    }

    @PostMapping("/top-up")
    public WalletResponseDto topUpWallet(@Valid @RequestBody TopUpWalletDto dto){
        return this.walletService.topUpWallet(dto.getAmount());
    }
}
