package com.algaworks.algashop.ordering.domain.valueobject.id;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record ShoppingCartId(UUID value) {

    public ShoppingCartId {
        requireNonNull(value);
    }

    public static ShoppingCartId of() {
        return new ShoppingCartId(GeneratorId.generateTimeBasedUUID());
    }

    public static ShoppingCartId from(String value) {
        return new ShoppingCartId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
