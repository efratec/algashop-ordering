package com.algaworks.algashop.ordering.core.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;

import java.time.OffsetDateTime;

public record ShoppingCartCreatedEvent(ShoppingCartId shoppingCartId,
                                       CustomerId customerId,
                                       OffsetDateTime createdAt) {

    public static ShoppingCartCreatedEvent of(ShoppingCartId shoppingCartId,
                                              CustomerId customerId,
                                              OffsetDateTime createdAt) {
        return new ShoppingCartCreatedEvent(shoppingCartId, customerId, createdAt);
    }

}
