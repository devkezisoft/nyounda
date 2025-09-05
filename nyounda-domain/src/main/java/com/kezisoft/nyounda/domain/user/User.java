package com.kezisoft.nyounda.domain.user;

import lombok.With;

import java.util.List;
import java.util.UUID;

@With
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
                UUID.randomUUID(),
                null,
                null,
                null,
                phoneNumber,
                List.of(UserRole.CLIENT)
        );
    }
}
