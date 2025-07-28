package com.kezisoft.nyounda.persistence.homeservice.jpa;

import com.kezisoft.nyounda.persistence.homeservice.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
}
