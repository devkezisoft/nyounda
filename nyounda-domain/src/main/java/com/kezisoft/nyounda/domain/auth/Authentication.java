package com.kezisoft.nyounda.domain.auth;

import com.kezisoft.nyounda.domain.user.UserRole;

import java.util.List;

public record Authentication(
        String principal,
        String token,
        List<UserRole> authorities

) {
}
