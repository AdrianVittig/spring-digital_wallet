package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.config.ModelMapperUtil;
import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.WalletRepository;
import com.vittig.spring_digital_wallet.dto.wallet.WalletResponseDto;
import com.vittig.spring_digital_wallet.exception.InvalidArgumentException;
import com.vittig.spring_digital_wallet.exception.InvalidAuthenticationException;
import com.vittig.spring_digital_wallet.exception.ObjectNotFoundException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private ModelMapperUtil modelMapper;

    @BeforeEach
    void setUp() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "testemail@google.com",
                        null
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentWalletShouldReturnWallet() {
        Wallet wallet = new Wallet();
        WalletResponseDto responseDto = mock(WalletResponseDto.class);

        when(walletRepository.findWalletByEmail("testemail@google.com"))
                .thenReturn(Optional.of(wallet));

        when(modelMapper.map(wallet, WalletResponseDto.class))
                .thenReturn(responseDto);

        WalletResponseDto result = walletService.getCurrentWallet();

        assertSame(responseDto, result);

        verify(walletRepository)
                .findWalletByEmail("testemail@google.com");

        verify(modelMapper)
                .map(wallet, WalletResponseDto.class);
    }

    @Test
    void getCurrentWalletShouldThrowWhenAuthenticationIsMissing() {
        SecurityContextHolder.clearContext();

        assertThrows(
                InvalidAuthenticationException.class,
                () -> walletService.getCurrentWallet()
        );

        verifyNoInteractions(walletRepository);
    }

    @Test
    void getCurrentWalletShouldThrowWhenWalletDoesNotExist() {
        when(walletRepository.findWalletByEmail("testemail@google.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> walletService.getCurrentWallet()
        );
    }

    @Test
    void createWalletShouldCreateWalletWithZeroBalanceAndIban() {
        User user = new User();

        when(walletRepository.saveAndFlush(any(Wallet.class)))
                .thenAnswer(invocation -> {
                    Wallet wallet = invocation.getArgument(0);

                    if (wallet.getId() == null) {
                        ReflectionTestUtils.setField(wallet, "id", 1L);
                    }

                    return wallet;
                });

        Wallet result = walletService.createWallet(user);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getCurrentBalance());
        assertNotNull(result.getIban());
        assertEquals("BG10000", result.getIban());
        assertSame(user, result.getUser());
        assertSame(result, user.getWallet());
        assertNotNull(result.getEntries());
        assertTrue(result.getEntries().isEmpty());

        verify(walletRepository, times(2))
                .saveAndFlush(any(Wallet.class));
    }

    @Test
    void createWalletShouldPropagateExceptionWhenRepositorySaveFails() {
        User user = new User();

        when(walletRepository.saveAndFlush(any(Wallet.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(
                RuntimeException.class,
                () -> walletService.createWallet(user)
        );

        verify(walletRepository)
                .saveAndFlush(any(Wallet.class));
    }

    @Test
    void topUpWalletShouldIncreaseBalance() {
        Wallet wallet = new Wallet();
        wallet.setCurrentBalance(new BigDecimal("100"));

        WalletResponseDto responseDto = mock(WalletResponseDto.class);

        when(walletRepository.findWalletByEmailForUpdate(
                "testemail@google.com"
        )).thenReturn(Optional.of(wallet));

        when(modelMapper.map(wallet, WalletResponseDto.class))
                .thenReturn(responseDto);

        WalletResponseDto result =
                walletService.topUpWallet(new BigDecimal("50"));

        assertEquals(
                new BigDecimal("150"),
                wallet.getCurrentBalance()
        );

        assertSame(responseDto, result);

        verify(walletRepository)
                .findWalletByEmailForUpdate("testemail@google.com");

        verify(walletRepository)
                .save(wallet);
    }

    @Test
    void topUpWalletShouldThrowWhenAmountIsZero() {
        assertThrows(
                InvalidArgumentException.class,
                () -> walletService.topUpWallet(BigDecimal.ZERO)
        );

        verifyNoInteractions(walletRepository);
    }

    @Test
    void topUpWalletShouldThrowWhenAmountIsNegative() {
        assertThrows(
                InvalidArgumentException.class,
                () -> walletService.topUpWallet(new BigDecimal("-10"))
        );

        verifyNoInteractions(walletRepository);
    }

    @Test
    void topUpWalletShouldThrowWhenAuthenticationIsMissing() {
        SecurityContextHolder.clearContext();

        assertThrows(
                InvalidAuthenticationException.class,
                () -> walletService.topUpWallet(new BigDecimal("50"))
        );

        verifyNoInteractions(walletRepository);
    }

    @Test
    void topUpWalletShouldThrowWhenWalletDoesNotExist() {
        when(walletRepository.findWalletByEmailForUpdate(
                "testemail@google.com"
        )).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> walletService.topUpWallet(new BigDecimal("50"))
        );
    }
}