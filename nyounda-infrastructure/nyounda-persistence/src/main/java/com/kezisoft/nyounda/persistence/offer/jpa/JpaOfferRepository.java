package com.kezisoft.nyounda.persistence.offer.jpa;

import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.persistence.offer.entity.OfferEntity;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface JpaOfferRepository extends JpaRepository<OfferEntity, UUID> {

    boolean existsByRequestAndUserAndStatusIn(
            ServiceRequestEntity request,
            UserEntity user,
            EnumSet<OfferStatus> statuses
    );

    @Query("""
                select o.request.id
                from OfferEntity o
                where o.user.id = :userId and o.request.id in :requestIds
            """)
    Set<UUID> findRequestIdsAppliedByUser(@Param("userId") UUID userId,
                                          @Param("requestIds") Collection<UUID> requestIds);

    /**
     * Returns tuples (offer, user, provider) for a request.
     * We join provider via its user. Provider is optional → LEFT JOIN.
     * <p>
     * NOTE:
     * - We do NOT try to read JSONB fields in JPQL; we fetch the OfferEntity as a whole,
     * and read `offer.getAmount()` in Java (it’s a mapped object).
     */
    @Query("""
                select o, u, p
                  from OfferEntity o
                  join o.user u
             left join com.kezisoft.nyounda.persistence.provider.entity.ProviderEntity p
                    on p.user = u
                 where o.request.id = :requestId
                   and o.status in :statuses
              order by o.createdAt desc
            """)
    List<Object[]> findRowsForRequest(
            @Param("requestId") UUID requestId,
            @Param("statuses") Collection<OfferStatus> statuses
    );
}
