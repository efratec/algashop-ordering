package com.algaworks.algashop.ordering.domain.valueobject;

import java.util.Objects;

public record ZipCode(String value) {

    public ZipCode {
        Objects.requireNonNull(value, "value is required");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value is required");
        }
        if (value.length() != 5) {
            throw new IllegalArgumentException("value length should be 5");
        }
    }

    @Override
    public String toString() {
        return this.value;
    }

}
