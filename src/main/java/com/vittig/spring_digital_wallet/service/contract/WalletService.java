package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.dto.wallet.WalletDto;

import java.math.BigDecimal;

public interface WalletService {
    WalletDto getCurrentWallet();
    WalletDto createWallet(User owner);
    Wallet getEntityByIdForUpdate(Long id);

    WalletDto topUpWallet(BigDecimal amount);
}
