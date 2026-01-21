package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.exception.DomainEntityNotFoundException;
import com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum;

import java.util.UUID;

public class ShoppingCartNotFoundException extends DomainEntityNotFoundException {

    public ShoppingCartNotFoundException() {
        super();
    }

    public ShoppingCartNotFoundException(String message) {
        super(message);
    }

    public static ShoppingCartNotFoundException because(UUID shoppingCartId) {
        return of(ShoppingCartNotFoundException::new, ReasonMessageEnum.SHOPPING_CART_IS_NOT_FOUND, shoppingCartId);
    }

}
