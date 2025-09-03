package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

import static com.algaworks.algashop.ordering.domain.model.exception.enums.OrderReason.NO_SHOPPING_CART_IS_NOT_AVAILABLE;

public class ShoppingCartCantProceedToCheckoutException extends DomainException {

    public ShoppingCartCantProceedToCheckoutException(String message) {
        super(message);
    }

}
