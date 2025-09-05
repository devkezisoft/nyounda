package com.kezisoft.nyounda.api.account.view;

import com.kezisoft.nyounda.domain.user.User;

import java.util.UUID;

public record UserView(
        UUID id,
        String fullName,
        String avatarUrl
) {
    public static UserView fromDomain(User user) {
        return new UserView(
                user.id(),
                user.fullName(),
                user.avatarUrl()
        );
    }
}
