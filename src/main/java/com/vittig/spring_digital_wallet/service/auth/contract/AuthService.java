package com.vittig.spring_digital_wallet.service.auth.contract;

import com.vittig.spring_digital_wallet.dto.auth.AuthResponseDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto registerUser(RegisterRequestDto registerRequestDto);

    void loginUser(LoginRequestDto loginRequestDto);
}
