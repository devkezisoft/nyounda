package com.kezisoft.nyounda.persistence.servicerequest.jpa;

import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
}
