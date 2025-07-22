package com.algaworks.algashop.ordering.domain.valueobject.id;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record ShoppingCartItemId(UUID value) {

    public ShoppingCartItemId {
        requireNonNull(value);
    }

    public static ShoppingCartItemId of() {
        return new ShoppingCartItemId(GeneratorId.generateTimeBasedUUID());
    }

    public static ShoppingCartItemId from(String value) {
        return new ShoppingCartItemId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
