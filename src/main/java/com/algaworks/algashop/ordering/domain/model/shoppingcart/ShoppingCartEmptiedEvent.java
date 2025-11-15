package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;

import java.time.OffsetDateTime;

public record ShoppingCartEmptiedEvent(ShoppingCartId shoppingCartId, CustomerId customerId, OffsetDateTime emptied) {

    public static ShoppingCartEmptiedEvent of(ShoppingCartId shoppingCartId,
                                              CustomerId customerId,
                                              OffsetDateTime emptiedAt) {
        return new ShoppingCartEmptiedEvent(shoppingCartId, customerId, emptiedAt);
    }

}
