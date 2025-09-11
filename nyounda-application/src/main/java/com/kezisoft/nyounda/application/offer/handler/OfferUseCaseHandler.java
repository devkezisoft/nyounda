// application/offer/ServiceOfferUseCase.java
package com.kezisoft.nyounda.application.offer.handler;

import com.kezisoft.nyounda.application.offer.command.OfferCreateCommand;
import com.kezisoft.nyounda.application.offer.port.in.OfferUseCase;
import com.kezisoft.nyounda.application.offer.port.out.OfferRepository;
import com.kezisoft.nyounda.application.servicerequest.port.in.ServiceRequestUseCase;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import com.kezisoft.nyounda.application.user.port.in.UserUseCase;
import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.offer.OfferId;
import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.UUID;

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

        // 2) user can’t offer on their own request
        if (req.user().id().equals(cmd.userId())) {
            throw new IllegalStateException("You cannot submit an offer on your own request.");
        }

        // 3) prevent duplicates
        if (offerRepository.existsActiveByRequestAndProvider(cmd.requestId(), cmd.userId(), EnumSet.of(OfferStatus.PENDING, OfferStatus.ACCEPTED))) {
            throw new IllegalStateException("You already have a pending offer for this job.");
        }

        // 4)if already rejected, can’t re-offer
        if (offerRepository.existsActiveByRequestAndProvider(cmd.requestId(), cmd.userId(), EnumSet.of(OfferStatus.REJECTED))) {
            throw new IllegalStateException("You cannot re-offer on a request that has already rejected your previous offer.");
        }

        // 5) build and persist
        Offer offer = cmd.toDomain(req, user, OfferStatus.PENDING);

        return offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void decline(OfferId offerId, UUID byClientId, String reason) {
// security check: offer.request.user.id == byClientId
        var offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));
        var request = serviceRequestUseCase.findById(offer.request().id())
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!request.user().id().equals(byClientId)) {
            throw new SecurityException("Not the owner of this request.");
        }
        offerRepository.markDeclined(offerId, reason);
    }

    @Override
    @Transactional
    public void choose(ServiceRequestId requestId, OfferId offerId, UUID byClientId) {
        // security check
        var request = serviceRequestUseCase.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!request.user().id().equals(byClientId)) {
            throw new SecurityException("Not the owner of this request.");
        }
        // accept selected
        offerRepository.markAccepted(offerId);
        // pin on request
        serviceRequestUseCase.choose(requestId, offerId);
        // (optional) auto reject others
        var others = offerRepository.findOtherPendingOfferIdsForRequest(requestId, offerId);

        offerRepository.bulkMarkRejected(others);

    }
}
