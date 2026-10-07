package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.config.ModelMapperUtil;
import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.entity.WalletEntry;
import com.vittig.spring_digital_wallet.data.repository.WalletEntryRepository;
import com.vittig.spring_digital_wallet.data.util.EntryType;
import com.vittig.spring_digital_wallet.service.contract.WalletEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class WalletEntryServiceImpl implements WalletEntryService {
    private final WalletEntryRepository walletEntryRepository;
    private final ModelMapperUtil modelMapper;

    @Override
    public void createWalletEntries(Transfer transfer, Wallet sender, Wallet recipient, BigDecimal amount) {
        WalletEntry senderWalletEntry = new WalletEntry();
        WalletEntry recipientWalletEntry = new WalletEntry();

        senderWalletEntry.setTransfer(transfer);
        senderWalletEntry.setWallet(sender);
        senderWalletEntry.setAmount(amount);
        senderWalletEntry.setEntryType(EntryType.OUTCOMING);

        recipientWalletEntry.setTransfer(transfer);
        recipientWalletEntry.setWallet(recipient);
        recipientWalletEntry.setAmount(amount);
        recipientWalletEntry.setEntryType(EntryType.INCOMING);

        transfer.getEntries().add(senderWalletEntry);
        transfer.getEntries().add(recipientWalletEntry);

        sender.getEntries().add(senderWalletEntry);
        recipient.getEntries().add(recipientWalletEntry);

        this.walletEntryRepository.save(senderWalletEntry);
        this.walletEntryRepository.save(recipientWalletEntry);
    }
}
