package com.kezisoft.nyounda.domain.homeservice;

import java.util.UUID;

public record HomeServiceId(UUID value) {
    public static HomeServiceId valueOf(UUID value) {
        return new HomeServiceId(value);
    }
}
