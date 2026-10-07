package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.entity.Wallet;

import java.math.BigDecimal;

public interface WalletEntryService {
    void createWalletEntries(Transfer transfer, Wallet wallet, Wallet recipient, BigDecimal amount);
}
