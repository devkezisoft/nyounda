// domain/offer/OfferUseCase.java
package com.kezisoft.nyounda.application.offer.port.in;

import com.kezisoft.nyounda.application.offer.command.OfferCreateCommand;
import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.offer.OfferId;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

import java.util.UUID;

public interface OfferUseCase {
    Offer create(OfferCreateCommand cmd);

    void decline(OfferId offerId, UUID byClientId, String reason);

    void choose(ServiceRequestId requestId, OfferId offerId, UUID byClientId);
}
