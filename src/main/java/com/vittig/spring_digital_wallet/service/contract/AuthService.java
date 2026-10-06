package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterSuccessfulDto;

public interface AuthService {
    LoginSuccessfulDto login(LoginRequestDto dto);
    RegisterSuccessfulDto register(RegisterRequestDto dto);
}
