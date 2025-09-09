package com.kezisoft.nyounda.persistence.servicerequest;

import com.kezisoft.nyounda.application.servicerequest.port.out.OfferReadPort;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.ServiceRequestNotFoundException;
import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.domain.servicerequest.Money;
import com.kezisoft.nyounda.domain.servicerequest.OfferCandidateView;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import com.kezisoft.nyounda.persistence.offer.entity.OfferEntity;
import com.kezisoft.nyounda.persistence.offer.jpa.JpaOfferRepository;
import com.kezisoft.nyounda.persistence.provider.entity.ProviderEntity;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.servicerequest.jpa.JpaServiceRequestRepository;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import com.kezisoft.nyounda.persistence.user.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
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

    @Override
    public List<OfferCandidateView> findCandidates(ServiceRequestId requestId) {
        List<Object[]> rows = repo.findRowsForRequest(
                requestId.value(),
                EnumSet.of(OfferStatus.PENDING)
        );

        return rows.stream()
                .map(row -> {
                    OfferEntity o = (OfferEntity) row[0];
                    UserEntity u = (UserEntity) row[1];
                    ProviderEntity p = (row.length > 2) ? (ProviderEntity) row[2] : null;


                    // double rating = (p != null) ? p.getAverageRating() : 100d;
                    // int reviewsCount = (p != null) ? p.getNumberOfReviews() : 55;
                    Money amount = o.getTotalAmount();

                    return new OfferCandidateView(
                            o.getId(),
                            u.getId(),
                            Optional.ofNullable(u.getFullName()).orElse(""),
                            u.getAvatarUrl(),
                            4.5,
                            5,
                            10d, // distanceKm (if/when you compute it)
                            o.getMode(),
                            amount,
                            o.getMessage(),
                            o.getCreatedAt()
                    );
                }).toList();
    }
}