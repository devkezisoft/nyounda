package com.kezisoft.nyounda.domain.categories;

import java.util.UUID;

public record CategoryId(UUID value) {
    public static CategoryId valueOf(UUID value) {
        return new CategoryId(value);
    }
}
