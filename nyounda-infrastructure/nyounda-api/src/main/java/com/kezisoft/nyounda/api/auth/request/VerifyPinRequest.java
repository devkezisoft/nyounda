package com.kezisoft.nyounda.api.auth.request;

import com.kezisoft.nyounda.application.auth.command.VerifyPinCommand;

public record VerifyPinRequest(String phone, String pinCode) {
    public VerifyPinCommand toCommand() {
        return new VerifyPinCommand(phone, pinCode);
    }
}
