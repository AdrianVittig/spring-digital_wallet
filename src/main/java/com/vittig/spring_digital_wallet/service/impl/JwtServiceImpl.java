package com.vittig.spring_digital_wallet.service.impl;

import com.vittig.spring_digital_wallet.data.entity.User;
import com.vittig.spring_digital_wallet.service.contract.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtEncoder jwtEncoder;
    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(600))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }
}
