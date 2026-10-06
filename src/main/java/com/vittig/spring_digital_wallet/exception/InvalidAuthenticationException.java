package com.vittig.spring_digital_wallet.exception;

public class InvalidAuthenticationException extends RuntimeException {
    public InvalidAuthenticationException(String message) {
        super(message);
    }
}
