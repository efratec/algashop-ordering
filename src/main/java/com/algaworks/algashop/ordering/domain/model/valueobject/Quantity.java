package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.io.Serializable;
import java.util.Objects;

public record Quantity(Integer value) implements Serializable, Comparable<Quantity> {

    public static final Quantity ZERO = of(0);

    public Quantity {
        Objects.requireNonNull(value);
        if (value < 0) {
            throw new IllegalArgumentException();
        }
    }

    public static Quantity of(Integer value) {
        return new Quantity(value);
    }

    public Quantity add(Quantity quantity) {
        Objects.requireNonNull(quantity);
        return Quantity.of(this.value + quantity.value);
    }

    @Override
    public int compareTo(Quantity quantity) {
        return this.value.compareTo(quantity.value);
    }

}
