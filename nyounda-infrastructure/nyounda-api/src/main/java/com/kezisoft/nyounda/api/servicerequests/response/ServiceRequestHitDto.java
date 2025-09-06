package com.kezisoft.nyounda.api.servicerequests.response;

import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceRequestHitDto(
        UUID id,
        String owner,
        String emoji,
        String title,
        String city,
        Integer distanceKm,
        boolean viewed,
        LocalDateTime postedAt
) {
    public static ServiceRequestHitDto from(ServiceRequest serviceRequest) {
        return new ServiceRequestHitDto(
                serviceRequest.id().value(),
                serviceRequest.user().fullName(),
                serviceRequest.category().emoji(),
                serviceRequest.title(),
                serviceRequest.address(),
                20,
                RandomUtils.secure().randomBoolean(),
                serviceRequest.createdAt()
        );
    }
}
