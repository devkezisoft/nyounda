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
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class OfferRepositoryAdapter implements OfferRepository {

    private final JpaOfferRepository repo;
    private final JpaServiceRequestRepository jpaRequestRepo;
    private final JpaUserRepository jpaUserRepo;

    @Override
    public Offer save(Offer offer) {

        ServiceRequestEntity req = jpaRequestRepo.findById(offer.request().id().value())
                .orElseThrow(() -> new NotFoundException("Service request not found: " + offer.request().id(), "servicerequest", "servicerequestNotFound"));

        UserEntity user = jpaUserRepo.findById(offer.user().id())
                .orElseThrow(AccountNotFoundException::new);

        OfferEntity entity = OfferEntity.fromDomain(offer, req, user);
        return repo.save(entity).toDomain();
    }

    @Override
    public Optional<Offer> findById(OfferId id) {
        return repo.findById(id.value()).map(OfferEntity::toDomain);
    }

    @Override
    public boolean existsActiveByRequestAndProvider(ServiceRequestId requestId, UUID userId) {
        ServiceRequestEntity req = jpaRequestRepo.findById(requestId.value())
                .orElseThrow(() -> new ServiceRequestNotFoundException(requestId.value()));

        UserEntity user = jpaUserRepo.findById(userId)
                .orElseThrow(AccountNotFoundException::new);

        return repo.existsByRequestAndUserAndStatusIn(
                req, user, EnumSet.of(OfferStatus.PENDING, OfferStatus.ACCEPTED)
        );
    }

    @Override
    public Set<UUID> findRequestIdsAppliedByUser(UUID userId, Collection<UUID> reqIds) {
        return repo.findRequestIdsAppliedByUser(userId, reqIds);
    }
}
