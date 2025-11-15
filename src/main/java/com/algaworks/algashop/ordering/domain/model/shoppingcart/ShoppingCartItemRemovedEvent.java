package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;

import java.time.OffsetDateTime;

public record ShoppingCartItemRemovedEvent(ShoppingCartId shoppingCartId,
                                           CustomerId customerId,
                                           ProductId productId,
                                           OffsetDateTime removedAt) {

    public static ShoppingCartItemRemovedEvent of(ShoppingCartId shoppingCartId,
                                                  CustomerId customerId,
                                                  ProductId productId,
                                                  OffsetDateTime removedAt) {
        return new ShoppingCartItemRemovedEvent(shoppingCartId, customerId, productId, removedAt);
    }

}
