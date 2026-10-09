package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.config.ModelMapperUtil;
import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.TransferRepository;
import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferRequestDto;
import com.vittig.spring_digital_wallet.service.contract.AuthService;
import com.vittig.spring_digital_wallet.service.contract.WalletEntryService;
import com.vittig.spring_digital_wallet.service.contract.WalletService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @InjectMocks
    private TransferServiceImpl transferServiceImpl;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AuthService authService;

    @Mock
    private WalletService walletService;

    @Mock
    private WalletEntryService walletEntryService;

    @Mock
    private ModelMapperUtil modelMapper;

    @BeforeEach
    void setup() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "testemail@gmail.com",
                        null
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTransferShouldTransferMoneySuccessfully() {
        User sender = new User();
        sender.setEmail("testemail@gmail.com");

        Wallet senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setIban("BG-SENDER");
        senderWallet.setCurrentBalance(new BigDecimal("100.00"));

        Wallet recipientWallet = new Wallet();
        recipientWallet.setId(2L);
        recipientWallet.setIban("BG-RECIPIENT");
        recipientWallet.setCurrentBalance(new BigDecimal("20.00"));

        sender.setWallet(senderWallet);
        senderWallet.setUser(sender);

        TransferRequestDto dto = new TransferRequestDto();
        dto.setToIban("BG-RECIPIENT");
        dto.setAmount(new BigDecimal("30.00"));

        when(transferRepository.save(any(Transfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(authService.findByEmail("testemail@gmail.com"))
                .thenReturn(Optional.of(sender));

        when(walletService.getWalletEntityByIban("BG-RECIPIENT"))
                .thenReturn(recipientWallet);

        when(walletService.getWalletByIdForUpdate(1L))
                .thenReturn(senderWallet);

        when(walletService.getWalletByIdForUpdate(2L))
                .thenReturn(recipientWallet);

        TransferDto expectedDto = new TransferDto();
        expectedDto.setAmount(new BigDecimal("30.00"));
        expectedDto.setFromIban("BG-SENDER");
        expectedDto.setToIban("BG-RECIPIENT");

        when(modelMapper.map(any(Transfer.class), eq(TransferDto.class)))
                .thenReturn(expectedDto);

        TransferDto result = transferServiceImpl.createTransfer(dto);

        assertNotNull(result);

        assertEquals(
                new BigDecimal("70.00"),
                senderWallet.getCurrentBalance()
        );

        assertEquals(
                new BigDecimal("50.00"),
                recipientWallet.getCurrentBalance()
        );

        assertEquals(
                new BigDecimal("30.00"),
                result.getAmount()
        );

        assertEquals("BG-SENDER", result.getFromIban());
        assertEquals("BG-RECIPIENT", result.getToIban());

        verify(walletEntryService).createWalletEntries(
                any(Transfer.class),
                eq(senderWallet),
                eq(recipientWallet),
                eq(new BigDecimal("30.00"))
        );
    }
}