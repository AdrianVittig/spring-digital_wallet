package com.vittig.spring_digital_wallet.dto.auth.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSuccessfulDto {
    private String token;
}
