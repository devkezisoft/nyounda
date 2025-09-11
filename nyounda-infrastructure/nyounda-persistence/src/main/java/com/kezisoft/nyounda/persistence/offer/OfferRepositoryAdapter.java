// persistence/offer/OfferRepositoryAdapter.java
package com.kezisoft.nyounda.persistence.offer;

import com.kezisoft.nyounda.application.offer.port.out.OfferRepository;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.NotFoundException;
import com.kezisoft.nyounda.application.shared.exception.ServiceRequestNotFoundException;
import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.offer.OfferId;
import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import com.kezisoft.nyounda.persistence.offer.entity.OfferEntity;
import com.kezisoft.nyounda.persistence.offer.jpa.JpaOfferRepository;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.servicerequest.jpa.JpaServiceRequestRepository;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import com.kezisoft.nyounda.persistence.user.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OfferRepositoryAdapter implements OfferRepository {

    private final JpaOfferRepository repo;
    private final JpaServiceRequestRepository jpaRequestRepo;
    private final JpaUserRepository jpaUserRepo;

    @Override
    public Offer save(Offer offer) {
        log.debug("Saving offer: {}", offer);
        ServiceRequestEntity req = jpaRequestRepo.findById(offer.request().id().value())
                .orElseThrow(() -> new NotFoundException("Service request not found: " + offer.request().id(), "servicerequest", "servicerequestNotFound"));

        UserEntity user = jpaUserRepo.findById(offer.user().id())
                .orElseThrow(AccountNotFoundException::new);

        OfferEntity entity = OfferEntity.fromDomain(offer, req, user);
        return repo.save(entity).toDomain(false);
    }

    @Override
    public Optional<Offer> findById(OfferId id) {
        log.debug("Finding offer by id: {}", id);
        return repo.findById(id.value()).map(offerEntity -> offerEntity.toDomain(false));
    }

    @Override
    public boolean existsActiveByRequestAndProvider(ServiceRequestId requestId, UUID userId, EnumSet<OfferStatus> statuses) {
        log.debug("Checking existence of active offer for requestId: {} and userId: {}", requestId, userId);
        ServiceRequestEntity req = jpaRequestRepo.findById(requestId.value())
                .orElseThrow(() -> new ServiceRequestNotFoundException(requestId.value()));

        UserEntity user = jpaUserRepo.findById(userId)
                .orElseThrow(AccountNotFoundException::new);

        return repo.existsByRequestAndUserAndStatusIn(
                req, user, statuses
        );
    }

    @Override
    public Set<UUID> findRequestIdsAppliedByUser(UUID userId, Collection<UUID> reqIds) {
        log.debug("Finding request IDs applied by userId: {} within reqIds: {}", userId, reqIds);
        return repo.findRequestIdsAppliedByUser(userId, reqIds);
    }

    @Override
    public void markDeclined(OfferId offerId, String reason) {
        int updatedCount = repo.markDeclined(offerId.value(), reason);
        if (updatedCount == 0) {
            log.warn("No offer was marked as declined for offerId: {}", offerId);
        }
    }

    @Override
    public void markAccepted(OfferId offerId) {
        int updatedCount = repo.markAccepted(offerId.value());
        if (updatedCount == 0) {
            log.warn("No offer was marked as accepted for offerId: {}", offerId);
        }
    }

    @Override
    public List<OfferId> findOtherPendingOfferIdsForRequest(ServiceRequestId requestId, OfferId exceptOfferId) {
        return repo.findOtherPending(requestId.value(), exceptOfferId.value())
                .stream()
                .map(OfferId::of)
                .toList();
    }

    @Override
    public void bulkMarkRejected(Collection<OfferId> offerIds) {
        if (CollectionUtils.isEmpty(offerIds)) {
            log.debug("No offer IDs provided for bulk rejection.");
            return;
        }
        int updatedCount = repo.bulkReject(offerIds.stream().map(OfferId::value).toList());
        log.debug("Bulk rejected {} offers.", updatedCount);
    }
}
