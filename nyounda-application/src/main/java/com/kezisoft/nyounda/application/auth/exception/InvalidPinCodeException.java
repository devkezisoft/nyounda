package com.kezisoft.nyounda.application.auth.exception;

public class InvalidPinCodeException extends RuntimeException {

    public InvalidPinCodeException() {
        super("Invalid or expired PIN code.");
    }

    public InvalidPinCodeException(String message) {
        super(message);
    }
}
