package com.kezisoft.nyounda.application.user.command;

import com.kezisoft.nyounda.domain.user.RegistrationType;
import com.kezisoft.nyounda.domain.user.User;

import java.util.UUID;

public record RegisterUserCommand(
        String fullName,
        String email,
        String phone,
        RegistrationType registrationType
) {
    public static User toDomain(RegisterUserCommand registerUserCommand) {
        return new User(
                UUID.randomUUID(),
                registerUserCommand.fullName(),
                null,
                registerUserCommand.email(),
                registerUserCommand.phone(),
                RegistrationType.toDomain(registerUserCommand.registrationType())
        );
    }
}
