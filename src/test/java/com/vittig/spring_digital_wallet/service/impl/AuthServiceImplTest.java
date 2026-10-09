package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.entity.Wallet;
import com.vittig.spring_digital_wallet.data.repository.AuthRepository;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterSuccessfulDto;
import com.vittig.spring_digital_wallet.exception.InputValidationException;
import com.vittig.spring_digital_wallet.exception.InvalidAuthenticationException;
import com.vittig.spring_digital_wallet.service.contract.AuthService;
import com.vittig.spring_digital_wallet.service.contract.JwtService;
import com.vittig.spring_digital_wallet.service.contract.WalletService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    void loginShouldReturnTokenWhenCredentialsAreCorrect() {
        User user = new User();
        user.setEmail("testemail@gmail.com");
        user.setPassword("encodedPassword");

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("testemail@gmail.com");
        dto.setPassword("12345678");

        when(authRepository.findByEmail("testemail@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("12345678", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("test-token");

        LoginSuccessfulDto result = authServiceImpl.login(dto);

        assertNotNull(result);
        assertEquals("test-token", result.getToken());
    }

    @Test
    void loginShouldThrowExceptionWhenCredentialsAreIncorrect() {
        User user = new User();
        user.setEmail("testemail@gmail.com");
        user.setPassword("encodedPassword");

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("testemail@gmail.com");
        dto.setPassword("1234ss5678");

        when(authRepository.findByEmail("testemail@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("1234ss5678", "encodedPassword"))
                .thenReturn(false);

        assertThrows(InputValidationException.class, () -> authServiceImpl.login(dto));
    }

    @Test
    void registerShouldReturnTokenWhenRegistrationIsSuccessful() {
        User user = new User();
        user.setEmail("testemail@gmail.com");
        user.setPassword("encodedPassword");

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("testemail@gmail.com");
        dto.setPassword("1234ss5678");
        dto.setConfirmPassword("1234ss5678");

        when(authRepository.existsByEmail("testemail@gmail.com")).thenReturn(false);

        when(passwordEncoder.encode("1234ss5678")).thenReturn("encodedPassword");

        when(walletService.createWallet(any(User.class)))
                .thenReturn(new Wallet());

        when(authRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("token");

        RegisterSuccessfulDto result = authServiceImpl.register(dto);

        assertNotNull(result);
        assertEquals("token", result.getToken());
    }

    @Test
    void registerShouldThrowExceptionWhenRegistrationIsNotSuccessful() {
        User user = new User();
        user.setEmail("testemail@gmail.com");
        user.setPassword("encodedPassword");

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("testemail@gmail.com");
        dto.setPassword("1234ss5678");
        dto.setConfirmPassword("1234ss5678");

        when(authRepository.existsByEmail("testemail@gmail.com")).thenReturn(true);

        assertThrows(InputValidationException.class, () -> authServiceImpl.register(dto));
    }
}