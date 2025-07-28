package com.kezisoft.nyounda.persistence.homeservice.jpa;

import com.kezisoft.nyounda.persistence.homeservice.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaReviewRepository extends JpaRepository<ReviewEntity, UUID> {
}
