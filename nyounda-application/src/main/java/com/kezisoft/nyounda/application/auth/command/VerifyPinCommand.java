package com.kezisoft.nyounda.application.auth.command;

public record VerifyPinCommand(String phoneNumber, String pinCode) {
}
