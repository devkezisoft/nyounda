package com.kezisoft.nyounda.domain.servicerequest;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {
    public static Money euro(BigDecimal amount) {
        return new Money(amount, "EUR");
    }

    public static Money xaf(BigDecimal amount) {
        return new Money(amount, "XAF");
    }

    public static Money xaf(double amount) {
        return xaf(BigDecimal.valueOf(amount));
    }
}