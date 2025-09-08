// application/offer/ServiceOfferUseCase.java
package com.kezisoft.nyounda.application.offer.handler;

import com.kezisoft.nyounda.application.offer.command.OfferCreateCommand;
import com.kezisoft.nyounda.application.offer.port.in.OfferUseCase;
import com.kezisoft.nyounda.application.offer.port.out.OfferRepository;
import com.kezisoft.nyounda.application.servicerequest.port.in.ServiceRequestUseCase;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import com.kezisoft.nyounda.application.user.port.in.UserUseCase;
import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfferUseCaseHandler implements OfferUseCase {

    private final OfferRepository offerRepository;
    private final ServiceRequestUseCase serviceRequestUseCase;
    private final UserUseCase userUseCase;

    @Override
    @Transactional
    public Offer create(OfferCreateCommand cmd) {
        // 1) request must exist
        ServiceRequest req = serviceRequestUseCase.findById(cmd.requestId())
                .orElseThrow(() -> new IllegalArgumentException("Service request not found"));
        var user = userUseCase.getById(cmd.userId()).orElseThrow(AccountNotFoundException::new);

        // 2) (optional) user can’t offer on their own request
        if (req.user().id().equals(cmd.userId())) {
            throw new IllegalStateException("You cannot submit an offer on your own request.");
        }

        // 3) (optional) prevent duplicates
        if (offerRepository.existsActiveByRequestAndProvider(cmd.requestId(), cmd.userId())) {
            throw new IllegalStateException("You already have a pending offer for this job.");
        }

        // 4) build and persist
        Offer offer = cmd.toDomain(req, user, OfferStatus.PENDING);

        return offerRepository.save(offer);
    }
}
