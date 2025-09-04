package com.kezisoft.nyounda.persistence.servicerequest;

import com.kezisoft.nyounda.application.servicerequest.port.out.ReviewRepository;
import com.kezisoft.nyounda.domain.servicerequest.Review;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ReviewEntity;
import com.kezisoft.nyounda.persistence.servicerequest.jpa.JpaReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepository {

    private final JpaReviewRepository repository;

    @Override
    public List<Review> findAllReviews(List<UUID> ids) {
        return repository.findAllById(ids)
                .stream()
                .map(ReviewEntity::toDomain)
                .toList();
    }
}
