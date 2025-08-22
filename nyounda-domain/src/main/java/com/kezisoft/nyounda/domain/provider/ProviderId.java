package com.kezisoft.nyounda.domain.provider;

import java.util.UUID;

public record ProviderId(UUID value) {
    public static ProviderId valueOf(UUID value) {
        return new ProviderId(value);
    }
}
