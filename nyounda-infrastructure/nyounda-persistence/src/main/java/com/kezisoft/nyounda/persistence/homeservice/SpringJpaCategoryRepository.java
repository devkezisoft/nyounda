package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.persistence.homeservice.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringJpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
}
