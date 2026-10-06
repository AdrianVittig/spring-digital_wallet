package com.vittig.spring_digital_wallet.exception;

public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String message) {
        super(message);
    }
}
