package com.kezisoft.nyounda.application.user.command;

import com.kezisoft.nyounda.domain.user.User;

public record UpdateUserCommand(
        String fullName,
        String email,
        String phone
) {

    public static User toDomain(UpdateUserCommand updateUserCommand) {
        return new User(
                null,
                updateUserCommand.fullName(),
                null,
                updateUserCommand.email(),
                updateUserCommand.phone(),
                null
        );
    }
}
