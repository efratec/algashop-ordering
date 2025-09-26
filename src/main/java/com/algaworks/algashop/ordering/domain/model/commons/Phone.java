package com.algaworks.algashop.ordering.domain.model.commons;

import java.util.Objects;

public record Phone(String value) {

    public Phone {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    public static Phone of(String value) {
        return new Phone(value);
    }

    @Override
    public String toString() {
        return this.value;
    }

}
