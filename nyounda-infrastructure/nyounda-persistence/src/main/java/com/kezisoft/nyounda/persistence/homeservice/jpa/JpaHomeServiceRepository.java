package com.kezisoft.nyounda.persistence.homeservice.jpa;

import com.kezisoft.nyounda.persistence.homeservice.entity.HomeServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaHomeServiceRepository extends JpaRepository<HomeServiceEntity, UUID> {
}
