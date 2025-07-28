package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.persistence.homeservice.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringJpaProviderRepository extends JpaRepository<ProviderEntity, UUID> {
}
