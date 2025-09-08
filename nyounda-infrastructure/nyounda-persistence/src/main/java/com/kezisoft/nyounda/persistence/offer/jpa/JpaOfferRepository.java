package com.kezisoft.nyounda.persistence.offer.jpa;

import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.persistence.offer.entity.OfferEntity;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

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
}
