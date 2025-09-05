package com.kezisoft.nyounda.api.account.request;

import com.kezisoft.nyounda.application.user.command.UpdateUserCommand;

public record AccountUpdateRequest(
        String fullName,
        String email,
        String phone
) {
    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(
                fullName,
                email,
                phone
        );
    }
}
