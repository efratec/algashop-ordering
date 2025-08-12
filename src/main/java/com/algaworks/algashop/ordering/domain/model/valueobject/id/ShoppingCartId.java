package com.algaworks.algashop.ordering.domain.model.valueobject.id;

import com.algaworks.algashop.ordering.domain.model.utility.GeneratorId;

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

    public static ShoppingCartId from(UUID value) {
        return new ShoppingCartId(value);
    }

}
