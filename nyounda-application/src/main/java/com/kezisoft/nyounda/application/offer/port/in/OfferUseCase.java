// domain/offer/OfferUseCase.java
package com.kezisoft.nyounda.application.offer.port.in;

import com.kezisoft.nyounda.application.offer.command.OfferCreateCommand;
import com.kezisoft.nyounda.domain.offer.Offer;

public interface OfferUseCase {
    Offer create(OfferCreateCommand cmd);
}
