package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId() {
        this(GeneratorId.generateTimeBasedUUID());
    }

    public CustomerId(UUID value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
