package com.kezisoft.nyounda.domain.servicerequest;

import java.time.LocalDateTime;
import java.util.UUID;

public record Review(
        UUID id,
        String authorName,
        String authorAvatarUrl,
        int rating,
        String comment,
        LocalDateTime date
) {
}
