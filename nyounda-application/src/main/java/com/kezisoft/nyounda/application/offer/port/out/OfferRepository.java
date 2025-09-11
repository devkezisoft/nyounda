// domain/offer/OfferRepository.java
package com.kezisoft.nyounda.application.offer.port.out;

import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.offer.OfferId;
import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

import java.util.*;

public interface OfferRepository {
    Offer save(Offer offer);

    Optional<Offer> findById(OfferId id);

    // optional: to prevent duplicates (one active offer per provider/request)
    boolean existsActiveByRequestAndProvider(ServiceRequestId requestId, UUID userId, EnumSet<OfferStatus> statuses);

    Set<UUID> findRequestIdsAppliedByUser(UUID userId, Collection<UUID> reqIds);

    void markDeclined(OfferId offerId, String reason);

    void markAccepted(OfferId offerId);

    List<OfferId> findOtherPendingOfferIdsForRequest(ServiceRequestId requestId, OfferId exceptOfferId);

    void bulkMarkRejected(Collection<OfferId> offerIds);
}
