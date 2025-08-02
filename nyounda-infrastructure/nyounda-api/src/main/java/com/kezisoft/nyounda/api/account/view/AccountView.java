package com.kezisoft.nyounda.api.account.view;

import com.kezisoft.nyounda.domain.user.User;
import com.kezisoft.nyounda.domain.user.UserRole;

import java.util.List;
import java.util.UUID;

public record AccountView(
        UUID id,
        String fullName,
        String avatarUrl,
        String email,
        String phone,
        List<UserRole> roles
) {

    public static AccountView fromDomain(User user) {
        return new AccountView(
                user.id(),
                user.fullName(),
                user.avatarUrl(),
                user.email(),
                user.phone(),
                user.roles()
        );
    }
}
