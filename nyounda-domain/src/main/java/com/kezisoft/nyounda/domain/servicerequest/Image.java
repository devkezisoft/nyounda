package com.kezisoft.nyounda.domain.servicerequest;

import java.util.UUID;

public record Image(
        UUID id,
        String url,
        String storageKey,
        String mimeType,
        long sizeBytes
) {
}
