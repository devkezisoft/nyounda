package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.persistence.homeservice.entity.ServiceImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringJpaServiceImageRepository extends JpaRepository<ServiceImageEntity, UUID> {
}
