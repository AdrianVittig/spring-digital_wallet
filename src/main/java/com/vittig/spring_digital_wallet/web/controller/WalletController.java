package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.dto.wallet.TopUpWalletDto;
import com.vittig.spring_digital_wallet.dto.wallet.WalletResponseDto;
import com.vittig.spring_digital_wallet.service.contract.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletService walletService;

    @GetMapping
    @Operation(
            summary = "Get current wallet",
            description = "Returns the wallet of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated"),
            @ApiResponse(responseCode = "404", description = "User or wallet not found")
    })
    public WalletResponseDto getCurrentWallet(){
        return this.walletService.getCurrentWallet();
    }

    @PostMapping("/top-up")
    @Operation(
            summary = "Top up wallet",
            description = "Adds funds to the authenticated user's wallet"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet topped up successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid top-up amount"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated"),
            @ApiResponse(responseCode = "404", description = "User or wallet not found")
    })
    public WalletResponseDto topUpWallet(@Valid @RequestBody TopUpWalletDto dto){
        return this.walletService.topUpWallet(dto.getAmount());
    }
}
