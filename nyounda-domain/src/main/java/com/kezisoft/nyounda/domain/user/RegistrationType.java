package com.kezisoft.nyounda.domain.user;

import java.util.List;

public enum RegistrationType {
    CLIENT,
    PROVIDER;

    public static List<UserRole> toDomain(RegistrationType registrationType) {
        return registrationType == CLIENT
                ? List.of(UserRole.CLIENT)
                : List.of(UserRole.CLIENT, UserRole.PROVIDER);
    }
}
