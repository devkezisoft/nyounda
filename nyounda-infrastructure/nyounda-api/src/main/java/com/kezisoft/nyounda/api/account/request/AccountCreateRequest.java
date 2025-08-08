package com.kezisoft.nyounda.api.account.request;

import com.kezisoft.nyounda.application.user.command.RegisterUserCommand;
import com.kezisoft.nyounda.domain.user.RegistrationType;

public record AccountCreateRequest(
        String fullName,
        String email,
        String phone,
        RegistrationType registrationType
) {
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(
                fullName,
                email,
                phone,
                registrationType
        );
    }
}
