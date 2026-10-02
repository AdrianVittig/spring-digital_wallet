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
import com.vittig.spring_digital_wallet.service.auth.contract.LedgerEntryService;
import com.vittig.spring_digital_wallet.service.auth.contract.TransferService;
import com.vittig.spring_digital_wallet.service.auth.contract.WalletService;
import com.vittig.spring_digital_wallet.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final WalletService walletService;
    private final LedgerEntryService ledgerEntryService;
    private final ModelMapperUtil modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TransferDto> getAllTransfers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        validateAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        Long walletId = user.getWallet().getId();

        return this.modelMapper.mapList(
                this.transferRepository.getAllTransfers(walletId),
                TransferDto.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransferDto getTransferById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        validateAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        Long walletId = user.getWallet().getId();

        Transfer transfer = this.transferRepository
                .getTransferById(walletId, id)
                .orElseThrow(() -> new ObjectNotFoundException("Transfer not found!"));

        return this.modelMapper.map(transfer, TransferDto.class);
    }

    @Override
    @Transactional
    public TransferDto createTransfer(Long toWalletId, BigDecimal amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        validateAuthentication(authentication);

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

        this.transferRepository.save(transfer);
        this.ledgerEntryService.createTransferEntries(transfer, sender, receiver, amount);

        return this.modelMapper.map(transfer, TransferDto.class);
    }

    private void validateInput(BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidInputException("Amount must be greater than zero!");
        }
    }

    private TransferWallets lockAndDetermineTransferWallets(Wallet wallet, Long toWalletId, BigDecimal amount){
        if(toWalletId == null){
            throw new InvalidInputException("Receiver Wallet should be correct!");
        }

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

    private void validateAuthentication(Authentication authentication){
        if(authentication == null || !(authentication.getPrincipal() instanceof User)){
            throw new ObjectNotFoundException("User not found!");
        }
    }
}
