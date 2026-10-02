package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.data.repository.AuthRepository;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginSuccessfulDto;
import com.vittig.spring_digital_wallet.service.contract.WalletService;
import com.vittig.spring_digital_wallet.util.JwtService;
import com.vittig.spring_digital_wallet.util.ModelMapperUtil;
import com.vittig.spring_digital_wallet.dto.auth.AuthResponseDto;
import com.vittig.spring_digital_wallet.dto.auth.login.LoginRequestDto;
import com.vittig.spring_digital_wallet.dto.auth.register.RegisterRequestDto;
import com.vittig.spring_digital_wallet.exception.InvalidInputException;
import com.vittig.spring_digital_wallet.exception.ObjectNotFoundException;
import com.vittig.spring_digital_wallet.service.contract.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapperUtil modelMapper;
    private final JwtService jwtService;
    private final WalletService walletService;

    @Override
    @Transactional
    public AuthResponseDto registerUser(RegisterRequestDto registerRequestDto){
        User user = new User();

        validateInputFields(registerRequestDto);

        String encodedPassword = this.passwordEncoder.encode(registerRequestDto.getPassword());

        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(encodedPassword);

        this.walletService.createWallet(user);

        return this.modelMapper.map(this.authRepository.save(user), AuthResponseDto.class);
    }

    @Override
    public LoginSuccessfulDto loginUser(LoginRequestDto loginRequestDto) {
        validateInputLoginFields(loginRequestDto);

        User user = this.authRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new ObjectNotFoundException("Invalid email or password!")
        );

        boolean passwordMatches = passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword());

        if(!passwordMatches){
            throw new InvalidInputException("Invalid email or password!");
        }

        String token = jwtService.generateToken(user);

        LoginSuccessfulDto loginSuccessfulDto = new LoginSuccessfulDto();

        loginSuccessfulDto.setToken(token);

        return loginSuccessfulDto;
    }

    private void validateInputFields(RegisterRequestDto registerRequestDto){
        if(registerRequestDto.getEmail() == null || registerRequestDto.getPassword() == null
                || registerRequestDto.getConfirmPassword() == null){
            throw new InvalidInputException("All fields are required!");
        }

        if(registerRequestDto.getEmail().isEmpty() || registerRequestDto.getPassword().isEmpty()
                || registerRequestDto.getConfirmPassword().isEmpty()){
            throw new InvalidInputException("All fields must be filled!");
        }

        if(registerRequestDto.getEmail().equals(" ") || registerRequestDto.getPassword().equals(" ")
                || registerRequestDto.getConfirmPassword().equals(" ")){
            throw new InvalidInputException("All fields must be filled!");
        }

        if(!registerRequestDto.getPassword().equals(registerRequestDto.getConfirmPassword())){
            throw new InvalidInputException("Passwords must match!");
        }

        boolean emailExists = this.authRepository.existsByEmail(registerRequestDto.getEmail());

        if(emailExists){
            throw new InvalidInputException("Email already exists!");
        }
    }

    private void validateInputLoginFields(LoginRequestDto loginRequestDto){
        if(loginRequestDto.getEmail() == null || loginRequestDto.getPassword() == null){
            throw new InvalidInputException("All fields are required!");
        }

        if(loginRequestDto.getEmail().isEmpty() || loginRequestDto.getPassword().isEmpty()){
            throw new InvalidInputException("All fields must be filled!");
        }

        if(loginRequestDto.getEmail().equals(" ") || loginRequestDto.getPassword().equals(" ")){
            throw new InvalidInputException("All fields must be filled!");
        }
    }
}
