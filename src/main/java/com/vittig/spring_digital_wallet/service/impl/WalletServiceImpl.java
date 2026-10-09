package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.config.ModelMapperUtil;
import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.WalletRepository;
import com.vittig.spring_digital_wallet.dto.wallet.WalletResponseDto;
import com.vittig.spring_digital_wallet.exception.InvalidArgumentException;
import com.vittig.spring_digital_wallet.exception.InvalidAuthenticationException;
import com.vittig.spring_digital_wallet.exception.ObjectNotFoundException;
import com.vittig.spring_digital_wallet.service.contract.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final ModelMapperUtil modelMapper;

    @Override
    public WalletResponseDto getCurrentWallet() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null){
            throw new InvalidAuthenticationException("Invalid Authentication!");
        }

        String email = auth.getName();

        return modelMapper.map(this.walletRepository.findWalletByEmail(email).orElseThrow(
                        () -> new ObjectNotFoundException("Wallet not found!")
                ), WalletResponseDto.class
        );
    }

    @Override
    public Wallet getWalletEntityByIban(String iban) {
        return this.walletRepository.findWalletByIban(iban).orElseThrow(
                () -> new ObjectNotFoundException("Wallet not found!")
        );
    }

    @Override
    public Wallet getWalletByIdForUpdate(Long id) {
        return this.walletRepository.findByIdForUpdate(id).orElseThrow(
                () -> new ObjectNotFoundException("Wallet not found!")
        );
    }

    @Override
    @Transactional
    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();

        wallet.setCurrentBalance(BigDecimal.ZERO);
        wallet.setEntries(new ArrayList<>());

        syncUserAndWallet(user, wallet);

        this.walletRepository.saveAndFlush(wallet);

        String iban = generateIban(wallet.getId());
        wallet.setIban(iban);

        return this.walletRepository.saveAndFlush(wallet);
    }

    @Override
    @Transactional
    public WalletResponseDto topUpWallet(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidArgumentException("Amount must be greater than 0!");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null){
            throw new InvalidAuthenticationException("Invalid Authentication!");
        }

        String email = auth.getName();

        Wallet wallet = this.walletRepository.findWalletByEmailForUpdate(email)
                .orElseThrow(() -> new ObjectNotFoundException("Wallet not found!"));

        wallet.setCurrentBalance(wallet.getCurrentBalance().add(amount));

        this.walletRepository.save(wallet);

        return modelMapper.map(wallet, WalletResponseDto.class);
    }

    private String generateIban(Long id){
        return "BG" + (9999 + id);
    }

    private void syncUserAndWallet(User user, Wallet wallet){
        user.setWallet(wallet);
        wallet.setUser(user);
    }

}
