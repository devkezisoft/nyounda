package com.kezisoft.nyounda.domain.auth;

public enum VerificationStatus {
    PENDING("pending"),
    APPROVED("approved"),
    CANCELED("canceled");

    private final String value;

    VerificationStatus(final String value) {
        this.value = value;
    }

    public static VerificationStatus forValue(String status) {
        if (status == null) {
            return CANCELED;
        }

        for (VerificationStatus v : values()) {
            if (v.toString().equalsIgnoreCase(status)) {
                return v;
            }
        }
        return CANCELED;
    }
}
