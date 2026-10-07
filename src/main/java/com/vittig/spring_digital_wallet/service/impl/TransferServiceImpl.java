package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.config.ModelMapperUtil;
import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.entity.WalletEntry;
import com.vittig.spring_digital_wallet.data.repository.TransferRepository;
import com.vittig.spring_digital_wallet.data.util.EntryType;
import com.vittig.spring_digital_wallet.dto.transfer.TransferDto;
import com.vittig.spring_digital_wallet.dto.transfer.TransferRequestDto;
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

        transferMoneyBetweenSenderAndRecipient(dto.getToIban(), transfer);

        return modelMapper.map(this.transferRepository.save(transfer), TransferDto.class);
    }

    @Transactional
    private void transferMoneyBetweenSenderAndRecipient(String toIban, Transfer transfer){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null){
            throw new InvalidAuthenticationException("Invalid Authentication!");
        }

        String email = auth.getName();

        User sender = this.authService.findByEmail(email).orElseThrow(
                () -> new ObjectNotFoundException("User not found!")
        );

        Wallet recipientWallet = this.walletService.getWalletEntityByIban(toIban);

        BigDecimal amount = transfer.getAmount();

        Wallet senderWallet = sender.getWallet();

        senderWallet.setCurrentBalance(senderWallet.getCurrentBalance().subtract(amount));
        recipientWallet.setCurrentBalance(recipientWallet.getCurrentBalance().add(amount));

        this.walletEntryService.createWalletEntries(transfer, senderWallet, recipientWallet, amount);
    }
}
