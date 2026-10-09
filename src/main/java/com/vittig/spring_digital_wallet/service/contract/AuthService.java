package com.vittig.spring_digital_wallet.service.contract;

import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterSuccessfulDto;

import java.util.Optional;

public interface AuthService {
    Optional<User> findByEmail(String email);
    LoginSuccessfulDto login(LoginRequestDto dto);
    RegisterSuccessfulDto register(RegisterRequestDto dto);
}
