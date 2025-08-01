package com.kezisoft.nyounda.application.auth.exception;

public class PinCodeGenerationCanceledException extends RuntimeException {
    public PinCodeGenerationCanceledException() {
        super("PIN code generation was canceled by the provider");
    }
}
