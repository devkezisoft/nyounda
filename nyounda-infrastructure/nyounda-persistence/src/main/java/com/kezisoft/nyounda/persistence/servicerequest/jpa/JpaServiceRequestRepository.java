package com.kezisoft.nyounda.persistence.servicerequest.jpa;

import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaServiceRequestRepository extends JpaRepository<ServiceRequestEntity, UUID> {
    List<ServiceRequestEntity> findByUser(UserEntity user);
}
