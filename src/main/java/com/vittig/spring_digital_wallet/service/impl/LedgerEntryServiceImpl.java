package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.data.entity.LedgerEntry;
import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.LedgerEntryRepository;
import com.vittig.spring_digital_wallet.data.util.MovementType;
import com.vittig.spring_digital_wallet.dto.l_entryDto.LedgerEntryDto;
import com.vittig.spring_digital_wallet.exception.ObjectNotFoundException;
import com.vittig.spring_digital_wallet.service.contract.LedgerEntryService;
import com.vittig.spring_digital_wallet.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerEntryServiceImpl implements LedgerEntryService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final ModelMapperUtil modelMapperUtil;

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

    @Override
    public List<LedgerEntryDto> getEntriesByTransferId(Long transferId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        validateAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        Wallet wallet = user.getWallet();

        List<LedgerEntry> entries = this.ledgerEntryRepository.getAllEntriesByTransfer(transferId, wallet.getId());

        return this.modelMapperUtil.mapList(entries, LedgerEntryDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDto> getCurrentWalletEntries() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        validateAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        Wallet wallet = user.getWallet();

        List<LedgerEntry> entries = this.ledgerEntryRepository.getAllEntriesByWallet(wallet.getId());

        return this.modelMapperUtil.mapList(entries, LedgerEntryDto.class);
    }

    private void validateAuthentication(Authentication authentication){
        if(authentication == null || !(authentication.getPrincipal() instanceof User)){
            throw new ObjectNotFoundException("User not found!");
        }
    }
}
