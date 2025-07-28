package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.persistence.homeservice.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringJpaReviewRepository extends JpaRepository<ReviewEntity, UUID> {
}
