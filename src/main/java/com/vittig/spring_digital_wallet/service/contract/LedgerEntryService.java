package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.Wallet;

import java.math.BigDecimal;

public interface LedgerEntryService {
    void createTransferEntries(Transfer transfer, Wallet sender, Wallet receiver, BigDecimal amount);
}
