package com.kezisoft.nyounda.domain.homeservice;

import java.util.UUID;

public record CategoryId(UUID value) {
    public static CategoryId valueOf(UUID value) {
        return new CategoryId(value);
    }
}
