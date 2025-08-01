package com.kezisoft.nyounda.application.auth.command;

public record VerifyPinCommand(String phone, String pinCode) {
}
