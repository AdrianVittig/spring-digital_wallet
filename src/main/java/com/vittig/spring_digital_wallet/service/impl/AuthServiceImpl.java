package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.AuthRepository;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterSuccessfulDto;
import com.vittig.spring_digital_wallet.exception.InputValidationException;
import com.vittig.spring_digital_wallet.exception.InvalidArgumentException;
import com.vittig.spring_digital_wallet.service.contract.AuthService;
import com.vittig.spring_digital_wallet.service.contract.JwtService;
import com.vittig.spring_digital_wallet.service.contract.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final WalletService walletService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByEmail(String email) {
        return this.authRepository.findByEmail(email);
    }

    @Override
    public LoginSuccessfulDto login(LoginRequestDto dto) {
        if(dto.getEmail() == null || dto.getPassword() == null){
            throw new InputValidationException("All fields are required!");
        }

        User user = this.authRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InputValidationException("Incorrect credentials!"));

        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new InputValidationException("Incorrect credentials!");
        }

        String token = jwtService.generateToken(user);

        LoginSuccessfulDto loginSuccessfulDto = new LoginSuccessfulDto();
        loginSuccessfulDto.setToken(token);
        return loginSuccessfulDto;
    }

    @Override
    @Transactional
    public RegisterSuccessfulDto register(RegisterRequestDto dto) {
        if(dto.getEmail() == null || dto.getPassword() == null || dto.getConfirmPassword() == null){
            throw new InputValidationException("All fields are required!");
        }

        if(dto.getPassword().length() < 8 || dto.getConfirmPassword().length() < 8){
            throw new InvalidArgumentException("Passwords must be at least 8 characters!");
        }

        if(!dto.getPassword().equals(dto.getConfirmPassword())){
            throw new InputValidationException("Passwords must match!");
        }

        boolean alreadyExists = this.authRepository.existsByEmail(dto.getEmail());

        if(alreadyExists){
            throw new InputValidationException("Email is already registered!");
        }

        User user = new User();

        user.setEmail(dto.getEmail());
        user.setPassword(this.passwordEncoder.encode(dto.getPassword()));

        Wallet wallet = this.walletService.createWallet(user);

        user.setWallet(wallet);

        User savedUser = this.authRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        RegisterSuccessfulDto registerSuccessfulDto = new RegisterSuccessfulDto();
        registerSuccessfulDto.setToken(token);
        return registerSuccessfulDto;
    }
}
