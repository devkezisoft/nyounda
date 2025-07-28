package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.application.homeservice.port.out.ReviewRepository;
import com.kezisoft.nyounda.domain.homeservice.Review;
import com.kezisoft.nyounda.persistence.homeservice.entity.ReviewEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaReviewRepository implements ReviewRepository {

    private final SpringJpaReviewRepository repository;

    @Override
    public List<Review> findAllReviews(List<UUID> ids) {
        return repository.findAllById(ids)
                .stream()
                .map(ReviewEntity::toDomain)
                .toList();
    }
}
