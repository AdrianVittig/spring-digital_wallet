package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.data.entity.LedgerEntry;
import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.LedgerEntryRepository;
import com.vittig.spring_digital_wallet.data.util.MovementType;
import com.vittig.spring_digital_wallet.service.contract.LedgerEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class LedgerEntryServiceImpl implements LedgerEntryService {

    private final LedgerEntryRepository ledgerEntryRepository;

    @Override
    public void createTransferEntries(Transfer transfer, Wallet sender, Wallet receiver, BigDecimal amount) {
        LedgerEntry senderEntry = new LedgerEntry();

        LedgerEntry receiverEntry = new LedgerEntry();

        senderEntry.setMovementType(MovementType.OUTCOME);
        senderEntry.setAmount(amount);
        senderEntry.setWallet(sender);
        senderEntry.setTransfer(transfer);
        transfer.getEntryList().add(senderEntry);

        receiverEntry.setMovementType(MovementType.INCOME);
        receiverEntry.setTransfer(transfer);
        receiverEntry.setAmount(amount);
        receiverEntry.setWallet(receiver);
        transfer.getEntryList().add(receiverEntry);

        this.ledgerEntryRepository.save(senderEntry);
        this.ledgerEntryRepository.save(receiverEntry);
    }
}
