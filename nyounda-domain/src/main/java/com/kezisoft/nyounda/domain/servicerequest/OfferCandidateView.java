package com.kezisoft.nyounda.domain.servicerequest;

import com.kezisoft.nyounda.domain.offer.OfferMode;

import java.time.LocalDateTime;
import java.util.UUID;

public record OfferCandidateView(
        UUID offerId,
        UUID userId,
        String userName,
        String userAvatarUrl,
        double rating,
        int reviewsCount,
        Double distanceKm,
        OfferMode mode,          // "HOURLY" | "FLAT"
        Money amount,     // e.g. cents
        String message,
        LocalDateTime createdAt
) {
}