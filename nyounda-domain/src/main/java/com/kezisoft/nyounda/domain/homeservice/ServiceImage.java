package com.kezisoft.nyounda.domain.homeservice;

import java.util.UUID;

public record ServiceImage(
        UUID id,
        String url,
        boolean isPrimary
) {
}
