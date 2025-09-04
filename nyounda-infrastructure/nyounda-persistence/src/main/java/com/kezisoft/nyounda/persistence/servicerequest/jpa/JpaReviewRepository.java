package com.kezisoft.nyounda.persistence.servicerequest.jpa;

import com.kezisoft.nyounda.persistence.servicerequest.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaReviewRepository extends JpaRepository<ReviewEntity, UUID> {
}
