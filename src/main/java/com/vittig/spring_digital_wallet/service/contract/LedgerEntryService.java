package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.data.entity.LedgerEntry;
import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.dto.l_entryDto.LedgerEntryDto;

import java.math.BigDecimal;
import java.util.List;

public interface LedgerEntryService {
    void createTransferEntries(Transfer transfer, Wallet sender, Wallet receiver, BigDecimal amount);
    List<LedgerEntryDto> getEntriesByTransferId(Long transferId);
    List<LedgerEntryDto> getCurrentWalletEntries();
}
