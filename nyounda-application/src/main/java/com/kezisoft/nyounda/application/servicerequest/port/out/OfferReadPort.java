package com.kezisoft.nyounda.application.servicerequest.port.out;

import com.kezisoft.nyounda.domain.servicerequest.OfferCandidateView;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

import java.util.List;
import java.util.UUID;

/**
 * Goal: service-request wants to know “has current user already applied to this request?” without depending on offer.
 * This is to avoid cyclic dependency between service-request and offer.
 * <p>
 * Note: we could have placed this method in OfferRepository, but that would create a cyclic dependency between
 * service-request and offer, which is not desirable.
 */
public interface OfferReadPort {
    boolean existsOfferForRequestAndUser(
            UUID requestId,
            UUID userId
    );

    List<OfferCandidateView> findCandidates(ServiceRequestId requestId);
}
