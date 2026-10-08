package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.config.ModelMapperUtil;
import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.TransferRepository;
import com.vittig.spring_digital_wallet.data.util.TransferParticipants;
import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferRequestDto;
import com.vittig.spring_digital_wallet.exception.InsufficientFundsException;
import com.vittig.spring_digital_wallet.exception.InvalidArgumentException;
import com.vittig.spring_digital_wallet.exception.InvalidAuthenticationException;
import com.vittig.spring_digital_wallet.exception.ObjectNotFoundException;
import com.vittig.spring_digital_wallet.service.contract.AuthService;
import com.vittig.spring_digital_wallet.service.contract.TransferService;
import com.vittig.spring_digital_wallet.service.contract.WalletEntryService;
import com.vittig.spring_digital_wallet.service.contract.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final WalletEntryService walletEntryService;
    private final WalletService walletService;
    private final AuthService authService;
    private final ModelMapperUtil modelMapper;

    @Override
    public List<TransferDto> getTransfersForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null){
            throw new InvalidAuthenticationException("Invalid Authentication!");
        }

        String email = auth.getName();

        User user = this.authService.findByEmail(email).orElseThrow(
                () -> new ObjectNotFoundException("User not found!")
        );

        return modelMapper.mapList(this.transferRepository.findTransfersForCurrentUser(user.getWallet().getIban()), TransferDto.class);
    }

    @Override
    @Transactional
    public TransferDto createTransfer(TransferRequestDto dto) {
        Transfer transfer = new Transfer();

        transfer.setAmount(dto.getAmount());
        transfer.setToIban(dto.getToIban());
        transfer.setEntries(new ArrayList<>());

        Transfer savedTransfer = this.transferRepository.save(transfer);

        transferMoneyBetweenSenderAndRecipient(dto.getToIban(), savedTransfer);

        return modelMapper.map(savedTransfer, TransferDto.class);
    }

    private void transferMoneyBetweenSenderAndRecipient(String toIban, Transfer transfer){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null){
            throw new InvalidAuthenticationException("Invalid Authentication!");
        }

        String email = auth.getName();

        User sender = this.authService.findByEmail(email).orElseThrow(
                () -> new ObjectNotFoundException("User not found!")
        );

        BigDecimal amount = transfer.getAmount();

        Wallet recipientWallet = this.walletService.getWalletEntityByIban(toIban);

        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidArgumentException("Amount must be greater than 0!");
        }

        if(sender.getWallet().getId().equals(recipientWallet.getId())){
            throw new InvalidArgumentException("Can not transfer money to yourself!");
        }

        TransferParticipants transferParticipants = lockAndTransferMoney(
                sender.getWallet().getId(),
                recipientWallet.getId(),
                amount);

        this.walletEntryService.createWalletEntries(transfer,
                transferParticipants.sender(),
                transferParticipants.recipient(),
                amount);
    }

    private TransferParticipants lockAndTransferMoney(Long senderWalletId, Long recipientWalletId, BigDecimal amount){
        Long firstToLock = Math.min(senderWalletId, recipientWalletId);

        Long secondToLock = Math.max(senderWalletId, recipientWalletId);

        Wallet senderWallet = null;
        Wallet recipientWallet = null;

        if(firstToLock.equals(senderWalletId)){
            senderWallet = this.walletService.getWalletByIdForUpdate(firstToLock);
            recipientWallet = this.walletService.getWalletByIdForUpdate(secondToLock);
        }
        else {
            recipientWallet = this.walletService.getWalletByIdForUpdate(firstToLock);
            senderWallet = this.walletService.getWalletByIdForUpdate(secondToLock);
        }

        if(senderWallet.getCurrentBalance().compareTo(amount) < 0){
            throw new InsufficientFundsException("Insufficient funds!");
        }

        senderWallet.setCurrentBalance(senderWallet.getCurrentBalance().subtract(amount));
        recipientWallet.setCurrentBalance(recipientWallet.getCurrentBalance().add(amount));

        return new TransferParticipants(senderWallet, recipientWallet);
    }
}
