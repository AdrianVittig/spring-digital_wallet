package com.vittig.spring_digital_wallet.data.util;

import com.vittig.spring_digital_wallet.data.entity.Wallet;

public record TransferParticipants(Wallet sender, Wallet recipient) {
}
