package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;

import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {

    public ProductId {
        Objects.requireNonNull(value, "Product id cannot be null");
    }

    public static ProductId of() {
        return new ProductId(GeneratorId.generateTimeBasedUUID());
    }

    public static ProductId from(String value) {
        return new ProductId(UUID.fromString(value));
    }

    public static ProductId from(UUID value) {
        return new ProductId(value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}
