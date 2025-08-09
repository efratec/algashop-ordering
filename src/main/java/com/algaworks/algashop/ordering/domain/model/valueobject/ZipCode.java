package com.algaworks.algashop.ordering.domain.model.valueobject;

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

    public static ZipCode of(String value) {
        return new ZipCode(value);
    }

    @Override
    public String toString() {
        return this.value;
    }

}
