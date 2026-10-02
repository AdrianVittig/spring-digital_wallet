package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.dto.auth.AuthResponseDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto registerUser(RegisterRequestDto registerRequestDto);

    LoginSuccessfulDto loginUser(LoginRequestDto loginRequestDto);
}
