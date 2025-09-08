// api/offers/dto/OfferDTO.java
package com.kezisoft.nyounda.api.offer.response;

import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.offer.OfferExpense;
import com.kezisoft.nyounda.domain.offer.OfferMode;
import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.domain.servicerequest.Money;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OfferView(
        UUID id,
        OfferMode mode,  // "HOURLY" | "FIXED"
        Money amount,
        List<OfferExpense> expenses,
        String message,
        OfferStatus status, // "PENDING" ...
        LocalDateTime createdAt
) {
    public static OfferView from(Offer offer) {
        return new OfferView(
                offer.id().value(),
                offer.mode(),
                offer.amount(),
                offer.expenses(),
                offer.message(),
                offer.status(),
                offer.createdAt()
        );
    }
}
