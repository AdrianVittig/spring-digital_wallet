package com.vittig.spring_digital_wallet.web.controller;

import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterSuccessfulDto;
import com.vittig.spring_digital_wallet.service.contract.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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
    @SecurityRequirements
    @Operation(
            summary = "Register user",
            description = "Registers a new user and returns a JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data or email already registered")
    })
    public RegisterSuccessfulDto register(@Valid @RequestBody RegisterRequestDto dto){
        return this.authService.register(dto);
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid login data"),
    })
    public LoginSuccessfulDto login(@Valid @RequestBody LoginRequestDto dto){
        return this.authService.login(dto);
    }
}
