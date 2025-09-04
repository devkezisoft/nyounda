package com.kezisoft.nyounda.application.homeservice.port.out;

import com.kezisoft.nyounda.domain.servicerequest.Review;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository {
    List<Review> findAllReviews(List<UUID> reviewIds);
}
