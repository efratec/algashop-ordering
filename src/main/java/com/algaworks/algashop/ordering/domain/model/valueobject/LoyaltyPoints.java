package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record LoyaltyPoints(Integer value) implements Comparable<LoyaltyPoints> {

    public static final LoyaltyPoints ZERO = new LoyaltyPoints(0);

    public LoyaltyPoints() {
        this(0);
    }

    public LoyaltyPoints {
        Objects.requireNonNull(value);
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
    }

    public LoyaltyPoints add(Integer value) {
        return add(new LoyaltyPoints(value));
    }

    public LoyaltyPoints add(LoyaltyPoints loyaltyPoints) {
        Objects.requireNonNull(value);
        if (loyaltyPoints.value() <= 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
        return new LoyaltyPoints(loyaltyPoints.value() + this.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(LoyaltyPoints o) {
        return this.value().compareTo(o.value());
    }

}
