package com.vittig.spring_digital_wallet.service.auth.impl;

import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.TransferRepository;
import com.vittig.spring_digital_wallet.data.util.TransferStatus;
import com.vittig.spring_digital_wallet.data.util.TransferWallets;
import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.exception.InvalidInputException;
import com.vittig.spring_digital_wallet.exception.ObjectNotFoundException;
import com.vittig.spring_digital_wallet.service.auth.contract.TransferService;
import com.vittig.spring_digital_wallet.service.auth.contract.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final WalletService walletService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public TransferDto createTransfer(Long toWalletId, BigDecimal amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !(authentication.getPrincipal() instanceof User)){
            throw new ObjectNotFoundException("User not found!");
        }

        User user = (User) authentication.getPrincipal();

        validateInput(amount);

        Wallet wallet = user.getWallet();

        if(wallet.getId().equals(toWalletId)){
            throw new InvalidInputException("Can not transfer to your wallet!");
        }

        TransferWallets transferWallets = lockAndDetermineTransferWallets(wallet, toWalletId, amount);

        Wallet sender = transferWallets.sender();

        Wallet receiver = transferWallets.receiver();

        Transfer transfer = new Transfer();

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        transfer.setFromWallet(sender);
        transfer.setToWallet(receiver);
        transfer.setAmount(amount);
        transfer.setCreatedAt(LocalDateTime.now());
        transfer.setStatus(TransferStatus.SUCCESSFUL);

        return this.modelMapper.map(this.transferRepository.save(transfer), TransferDto.class);
    }

    private void validateInput(BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidInputException("Amount must be greater than zero!");
        }
    }

    private TransferWallets lockAndDetermineTransferWallets(Wallet wallet, Long toWalletId, BigDecimal amount){
        long firstId = Math.min(wallet.getId(), toWalletId);

        Wallet firstLockedWallet = this.walletService.getEntityByIdForUpdate(firstId);

        long secondId = Math.max(wallet.getId(), toWalletId);

        Wallet secondLockedWallet = this.walletService.getEntityByIdForUpdate(secondId);

        Wallet sender =
                firstLockedWallet.getId().equals(wallet.getId()) ? firstLockedWallet : secondLockedWallet;

        Wallet receiver =
                !firstLockedWallet.getId().equals(wallet.getId()) ? firstLockedWallet : secondLockedWallet;

        if(sender.getBalance().compareTo(amount) < 0){
            throw new InvalidInputException("Insufficient funds!");
        }

        return new TransferWallets(sender, receiver);
    }
}
