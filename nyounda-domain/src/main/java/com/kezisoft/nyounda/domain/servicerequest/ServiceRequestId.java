package com.kezisoft.nyounda.domain.servicerequest;

import java.util.UUID;

public record ServiceRequestId(UUID value) {
    public static ServiceRequestId valueOf(UUID value) {
        return new ServiceRequestId(value);
    }
}
