package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterSuccessfulDto;
import com.vittig.spring_digital_wallet.service.contract.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public RegisterSuccessfulDto register(@Valid @RequestBody RegisterRequestDto dto){
        return this.authService.register(dto);
    }

    @PostMapping("/login")
    public LoginSuccessfulDto login(@Valid @RequestBody LoginRequestDto dto){
        return this.authService.login(dto);
    }
}
