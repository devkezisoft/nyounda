package com.kezisoft.nyounda.persistence.servicerequest.jpa;

import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaServiceRequestRepository extends JpaRepository<ServiceRequestEntity, UUID>, JpaSpecificationExecutor<ServiceRequestEntity> {
    @EntityGraph(attributePaths = {"images"})
    List<ServiceRequestEntity> findByUser(UserEntity user);

    @Override
    @EntityGraph(attributePaths = {"images"})
    Optional<ServiceRequestEntity> findById(UUID id);

    @Modifying
    @Query("update ServiceRequestEntity sr set sr.chosenOffer.id = :offerId, sr.status = com.kezisoft.nyounda.domain.servicerequest.ServiceRequestStatus.ASSIGNED where sr.id = :value")
    int choose(UUID value, UUID offerId);
}
