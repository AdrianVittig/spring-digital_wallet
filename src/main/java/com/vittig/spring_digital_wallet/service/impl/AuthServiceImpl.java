package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.repository.AuthRepository;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterSuccessfulDto;
import com.vittig.spring_digital_wallet.service.contract.AuthService;
import com.vittig.spring_digital_wallet.service.contract.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginSuccessfulDto login(LoginRequestDto dto) {
        User user = new User();

        user.setEmail(dto.getEmail());
        user.setPassword(this.passwordEncoder.encode(dto.getPassword()));

        String token = jwtService.generateToken(user);

        LoginSuccessfulDto loginSuccessfulDto = new LoginSuccessfulDto();
        loginSuccessfulDto.setToken(token);
        return loginSuccessfulDto;
    }

    @Override
    public RegisterSuccessfulDto register(RegisterRequestDto dto) {
        User user = new User();

        user.setEmail(dto.getEmail());
        user.setPassword(this.passwordEncoder.encode(dto.getPassword()));

        User savedUser = this.authRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        RegisterSuccessfulDto registerSuccessfulDto = new RegisterSuccessfulDto();
        registerSuccessfulDto.setToken(token);
        return registerSuccessfulDto;
    }
}
