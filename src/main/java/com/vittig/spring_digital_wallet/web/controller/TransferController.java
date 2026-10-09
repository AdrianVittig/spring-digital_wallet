package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferFilterRequestDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferRequestDto;
import com.vittig.spring_digital_wallet.service.contract.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get current user's transfers",
            description = "Returns transfer history for the authenticated user's wallet with optional filters")
    @ApiResponses
            ({
                    @ApiResponse(responseCode = "200", description = "Transfers retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthenticated"),
                    @ApiResponse(responseCode = "404", description = "User or wallet not found")
            }
    )
    public List<TransferDto> getTransfersForCurrentUser(TransferFilterRequestDto dto){
        return this.transferService.getTransfersForCurrentUser(dto);
    }

    @PostMapping
    @Operation(
            summary = "Create transfer",
            description = "Creates a transfer from the authenticated user's wallet to the specified recipient wallet"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer data or insufficient funds"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated"),
            @ApiResponse(responseCode = "404", description = "Recipient wallet not found")
    })
    public TransferDto createTransfer(@Valid @RequestBody TransferRequestDto dto){
        return this.transferService.createTransfer(dto);
    }
}
