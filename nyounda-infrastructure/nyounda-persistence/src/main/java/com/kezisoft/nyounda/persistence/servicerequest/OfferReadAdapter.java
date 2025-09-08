package com.kezisoft.nyounda.persistence.servicerequest;

import com.kezisoft.nyounda.application.servicerequest.port.out.OfferReadPort;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.ServiceRequestNotFoundException;
import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.persistence.offer.jpa.JpaOfferRepository;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.servicerequest.jpa.JpaServiceRequestRepository;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import com.kezisoft.nyounda.persistence.user.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OfferReadAdapter implements OfferReadPort {
    private final JpaOfferRepository repo;
    private final JpaServiceRequestRepository jpaRequestRepo;
    private final JpaUserRepository jpaUserRepo;

    @Override
    public boolean existsOfferForRequestAndUser(
            UUID requestId, UUID userId
    ) {
        ServiceRequestEntity req = jpaRequestRepo.findById(requestId)
                .orElseThrow(() -> new ServiceRequestNotFoundException(requestId));

        UserEntity user = jpaUserRepo.findById(userId)
                .orElseThrow(AccountNotFoundException::new);
        return repo.existsByRequestAndUserAndStatusIn(req, user, EnumSet.of(
                OfferStatus.PENDING,
                OfferStatus.ACCEPTED
        ));
    }
}