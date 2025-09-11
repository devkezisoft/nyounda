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

    public Money add(Money price) {
        if (!this.currency.equals(price.currency)) {
            throw new IllegalArgumentException("Cannot add Money with different currencies");
        }
        return new Money(this.amount.add(price.amount), this.currency);
    }
}