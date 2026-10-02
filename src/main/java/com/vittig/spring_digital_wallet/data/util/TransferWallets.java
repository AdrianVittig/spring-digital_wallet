package com.vittig.spring_digital_wallet.data.util;

import com.vittig.spring_digital_wallet.data.entity.Wallet;

public record TransferWallets(Wallet sender, Wallet receiver) {
}
