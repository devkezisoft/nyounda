package com.kezisoft.nyounda.persistence.provider.jpa;

import com.kezisoft.nyounda.persistence.provider.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaProviderRepository extends JpaRepository<ProviderEntity, UUID> {
    @Query("SELECT p FROM ProviderEntity p WHERE p.user.id = :userId")
    Optional<ProviderEntity> findByUserId(UUID userId);
}
