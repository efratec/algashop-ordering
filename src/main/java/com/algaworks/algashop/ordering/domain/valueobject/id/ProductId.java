package com.algaworks.algashop.ordering.domain.valueobject.id;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;

import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {

    public ProductId {
        Objects.requireNonNull(value, "Product id cannot be null");
    }

    public static ProductId generate() {
        return new ProductId(GeneratorId.generateTimeBasedUUID());
    }

    public static ProductId of(String value) {
        return new ProductId(UUID.fromString(value));
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}
