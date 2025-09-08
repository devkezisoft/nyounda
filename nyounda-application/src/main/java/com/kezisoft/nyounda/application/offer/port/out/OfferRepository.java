// domain/offer/OfferRepository.java
package com.kezisoft.nyounda.application.offer.port.out;

import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.offer.OfferId;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

import java.util.Optional;
import java.util.UUID;

public interface OfferRepository {
    Offer save(Offer offer);

    Optional<Offer> findById(OfferId id);

    // optional: to prevent duplicates (one active offer per provider/request)
    boolean existsActiveByRequestAndProvider(ServiceRequestId requestId, UUID userId);
}
