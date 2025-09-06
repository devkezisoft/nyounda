package com.kezisoft.nyounda.application.searchrequest.command;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

// application/dto
public record ServiceRequestSearchQuery(
        List<UUID> skillIds,
        String address,         // optional for geocoding/filter
        Integer radiusKm,       // optional
        Pageable pageable
) {
    public ServiceRequestSearchQuery fixed() {
        return new ServiceRequestSearchQuery(
                skillIds == null ? List.of() : skillIds,
                address,
                (radiusKm() == null || radiusKm() <= 0) ? 50 : radiusKm(),
                pageable
        );
    }
}


