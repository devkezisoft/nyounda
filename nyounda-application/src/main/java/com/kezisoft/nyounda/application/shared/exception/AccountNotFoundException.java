package com.kezisoft.nyounda.application.shared.exception;

public class AccountNotFoundException extends NotFoundException {
    public AccountNotFoundException() {
        super("User could not be found", "account", "accountNotFound");
    }
}
