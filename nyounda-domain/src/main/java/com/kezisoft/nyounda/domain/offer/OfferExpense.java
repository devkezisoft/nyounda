// domain/offer/OfferExpense.java
package com.kezisoft.nyounda.domain.offer;

import com.kezisoft.nyounda.domain.servicerequest.Money;

public record OfferExpense(String label, Money price) { // price in euros (or cents if you prefer)
    public static OfferExpense of(String label, Money price) {
        return new OfferExpense(label, price);
    }
}
