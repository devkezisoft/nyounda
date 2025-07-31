package com.kezisoft.nyounda.domain.user;

import java.util.List;
import java.util.UUID;

public record User(
        UUID id,
        String fullName,
        String avatarUrl,
        String email,
        String phone,
        List<UserRole> roles
) {
    public static User createFromPhoneNumber(String phoneNumber) {
        return new User(
                null,
                null,
                null,
                null,
                phoneNumber,
                List.of()
        );
    }
}
