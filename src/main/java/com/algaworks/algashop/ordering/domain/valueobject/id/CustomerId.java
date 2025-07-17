package com.algaworks.algashop.ordering.domain.valueobject.id;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId {
        Objects.requireNonNull(value);
    }

    public static CustomerId generate() {
        return new CustomerId(GeneratorId.generateTimeBasedUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}