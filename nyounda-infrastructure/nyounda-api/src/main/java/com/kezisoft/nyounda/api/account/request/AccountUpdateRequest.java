package com.kezisoft.nyounda.api.account.request;

import com.kezisoft.nyounda.application.user.command.UpdateUserCommand;
import org.springframework.lang.Nullable;

public record AccountUpdateRequest(
        @Nullable String fullName,
        @Nullable String email,
        @Nullable String phone
) {
    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(
                fullName,
                email,
                phone
        );
    }
}
