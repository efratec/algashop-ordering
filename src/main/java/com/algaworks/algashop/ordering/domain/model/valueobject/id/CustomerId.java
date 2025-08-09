package com.algaworks.algashop.ordering.domain.model.valueobject.id;

import com.algaworks.algashop.ordering.domain.model.utility.GeneratorId;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId {
        Objects.requireNonNull(value);
    }

    public static CustomerId of() {
        return new CustomerId(GeneratorId.generateTimeBasedUUID());
    }

    public static CustomerId from(UUID value) {
        return new CustomerId(value);
    }

}