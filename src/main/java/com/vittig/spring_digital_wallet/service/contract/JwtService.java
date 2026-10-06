package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.data.entity.User;

public interface JwtService {
    String generateToken(User user);
}
