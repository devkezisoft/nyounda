package com.kezisoft.nyounda.persistence.homeservice.jpa;

import com.kezisoft.nyounda.persistence.homeservice.entity.ServiceImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaServiceImageRepository extends JpaRepository<ServiceImageEntity, UUID> {
}
