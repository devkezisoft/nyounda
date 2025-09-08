package com.kezisoft.nyounda.api.servicerequests.response;

import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestSearchHit;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceRequestSearchHitView(
        UUID id,
        String owner,
        String emoji,
        String title,
        String city,
        double distanceKm,
        boolean applied,
        LocalDateTime postedAt
) {
    public static ServiceRequestSearchHitView from(ServiceRequestSearchHit hit) {
        return new ServiceRequestSearchHitView(
                hit.request().id().value(),
                hit.request().user().fullName(),
                hit.request().category().emoji(),
                hit.request().title(),
                hit.request().address(),
                RandomUtils.nextDouble(10.0, 1000.0), //TODO calculate real distance
                hit.applied(),
                hit.request().createdAt()
        );
    }
}
