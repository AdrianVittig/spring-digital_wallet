package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.config.ModelMapperUtil;
import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.WalletRepository;
import com.vittig.spring_digital_wallet.dto.wallet.WalletResponseDto;
import com.vittig.spring_digital_wallet.exception.InvalidArgumentException;
import com.vittig.spring_digital_wallet.exception.InvalidAuthenticationException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @InjectMocks
    private WalletServiceImpl walletServiceImpl;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private ModelMapperUtil modelMapper;

    @BeforeEach
    void setup(){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testemail@gmail.com",
                null
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentWalletShouldReturnWalletForAuthenticatedUser() {
        Wallet wallet = new Wallet();

        WalletResponseDto dto = new WalletResponseDto();
        dto.setCurrentBalance(BigDecimal.ZERO);

       when(walletRepository.findWalletByEmail("testemail@gmail.com"))
               .thenReturn(Optional.of(wallet));

       when(modelMapper.map(wallet, WalletResponseDto.class)).thenReturn(dto);

        WalletResponseDto result = walletServiceImpl.getCurrentWallet();

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getCurrentBalance());
    }

    @Test
    void createWalletShouldCreateWalletWithZeroBalanceAndIban() {
        User user = new User();
        user.setEmail("testemail@gmail.com");

        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletServiceImpl.createWallet(user);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getCurrentBalance());
        assertNotNull(result.getIban());
        assertTrue(result.getIban().startsWith("BG"));
        assertSame(user, result.getUser());
        assertSame(result, user.getWallet());
    }

    @Test
    void getCurrentWalletShouldThrowExceptionWhenAuthenticationIsMissing(){
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThrows(InvalidAuthenticationException.class,
                () -> walletServiceImpl.getCurrentWallet());
    }

    @Test
    void createWalletShouldPropagateExceptionWhenRepositorySaveFails() {
        User user = new User();
        user.setEmail("testemail@gmail.com");

        when(walletRepository.save(any(Wallet.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(
                RuntimeException.class,
                () -> walletServiceImpl.createWallet(user)
        );
    }

    @Test
    void topUpWalletShouldIncreaseBalance() {
        Wallet wallet = new Wallet();

        WalletResponseDto dto = new WalletResponseDto();
        dto.setCurrentBalance(new BigDecimal("5.00"));

        wallet.setCurrentBalance(BigDecimal.ZERO);

        when(walletRepository.findWalletByEmailForUpdate("testemail@gmail.com"))
                .thenReturn(Optional.of(wallet));

        when(modelMapper.map(wallet, WalletResponseDto.class)).thenReturn(dto);

        when(walletRepository.save(wallet)).thenReturn(wallet);

        WalletResponseDto result = walletServiceImpl.topUpWallet(new BigDecimal("5.00"));

        assertNotNull(result);
        assertEquals(new BigDecimal("5.00"), result.getCurrentBalance());
    }

    @Test
    void topUpWalletShouldThrowExceptionWhenAmountIsZeroOrNegative() {
        assertThrows(InvalidArgumentException.class,
                () -> walletServiceImpl.topUpWallet(new BigDecimal("-5.00")));
    }
}