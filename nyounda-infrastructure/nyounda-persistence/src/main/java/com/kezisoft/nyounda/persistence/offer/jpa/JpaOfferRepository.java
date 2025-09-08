package com.kezisoft.nyounda.persistence.offer.jpa;

import com.kezisoft.nyounda.persistence.offer.entity.OfferEntity;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaOfferRepository extends JpaRepository<OfferEntity, UUID> {

    boolean existsByRequestAndUser(
            ServiceRequestEntity request,
            UserEntity user
    );
}
