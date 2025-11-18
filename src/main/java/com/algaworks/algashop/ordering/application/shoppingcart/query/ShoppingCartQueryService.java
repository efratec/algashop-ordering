package com.algaworks.algashop.ordering.application.shoppingcart.query;

import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartOutput;

import java.util.UUID;

public interface ShoppingCartQueryService {

    ShoppingCartOutput findById(UUID shoppingCartId);
    ShoppingCartOutput findByCustomerId(UUID customerId);

}
