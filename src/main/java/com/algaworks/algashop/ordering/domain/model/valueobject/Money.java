package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal value) implements Comparable<Money> {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static Money ZERO() {
        return new Money(BigDecimal.ZERO);
    }

    public Money {
        Objects.requireNonNull(value);
        value = value.setScale(2, ROUNDING_MODE);
        if (value.signum() == -1) {
            throw new IllegalArgumentException(String.format("The value '%s' is invalid", value));
        }
    }

    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    public static Money of(String value) {
        return new Money(new BigDecimal(value));
    }

    public Money multiply(Quantity quantity) {
        Objects.requireNonNull(quantity);
        if (quantity.value() < 1) {
            throw new IllegalArgumentException(String.format("The value '%s' is invalid", quantity.value()));
        }
        var multiplied = this.value.multiply(BigDecimal.valueOf(quantity.value()));
        return new Money(multiplied);
    }

    public Money add(Money money) {
        Objects.requireNonNull(money);
        return new Money(this.value.add(money.value));
    }

    public Money divide(Money o) {
        return new Money(this.value.divide(o.value, ROUNDING_MODE));
    }

    @Override
    public int compareTo(Money money) {
        return this.value.compareTo(money.value);
    }

}
