package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.dto.wallet.WalletResponseDto;

import java.math.BigDecimal;

public interface WalletService {
    WalletResponseDto getCurrentWallet();

    Wallet createWallet(User user);

    WalletResponseDto topUpWallet(BigDecimal amount);
}
