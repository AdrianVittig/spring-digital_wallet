package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.dto.auth.AuthResponseDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;
import com.vittig.spring_digital_wallet.service.contract.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponseDto register(@RequestBody RegisterRequestDto dto){
        return this.authService.registerUser(dto);
    }

    @PostMapping("/login")
    public LoginSuccessfulDto login(@RequestBody LoginRequestDto dto){
        return this.authService.loginUser(dto);
    }
}
