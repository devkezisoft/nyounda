package com.kezisoft.nyounda.persistence.servicerequest;

import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

// infrastructure/jpa/ServiceRequestSpec.java
@Component
public class ServiceRequestSpec {

    public Specification<ServiceRequestEntity> hasAnySkill(List<UUID> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) return null;

        return (root, cq, cb) -> cb.or(
                // match by subcategory id
                root.get("subcategory").get("id").in(skillIds),
                // or by category id (optional — keep if you want category matches too)
                root.get("category").get("id").in(skillIds)
        );
    }

    public Specification<ServiceRequestEntity> isWithinRadius(String address, Integer radiusKm) {
        if (address == null || radiusKm == null) return null;
        // Implement geo filter if you have lat/lng; otherwise return null for now.
        return null;
    }

    public Specification<ServiceRequestEntity> notCanceled() {
        return (root, cq, cb) -> cb.not(root.get("status").in("CANCELED"));
    }
}
