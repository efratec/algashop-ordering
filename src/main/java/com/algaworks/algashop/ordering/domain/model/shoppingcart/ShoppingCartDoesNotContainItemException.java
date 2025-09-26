package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.NO_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM;

public class ShoppingCartDoesNotContainItemException extends DomainException {

    public ShoppingCartDoesNotContainItemException(String message) {
        super(message);
    }

    public static ShoppingCartDoesNotContainItemException because(ShoppingCartId id, ShoppingCartItemId itemId) {
        return DomainException.of(ShoppingCartDoesNotContainItemException::new,
                NO_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM, id, itemId);
    }

}
