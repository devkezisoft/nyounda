// domain/offer/OfferId.java
package com.kezisoft.nyounda.domain.offer;

import java.util.UUID;

public record OfferId(UUID value) {
    public static OfferId generate() {
        return OfferId.of(UUID.randomUUID());
    }

    public static OfferId of(UUID v) {
        return new OfferId(v);
    }
}
