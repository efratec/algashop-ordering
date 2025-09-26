package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.exception.DomainException;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.NO_ORDER_DOES_NOT_CONTAIN_ITEM;

public class ShoppingCartDoesNotContainProductException extends DomainException {

    public ShoppingCartDoesNotContainProductException(String message) {
        super(message);
    }

    public static ShoppingCartDoesNotContainProductException because(ShoppingCartId id, ProductId productId) {
        return DomainException.of(ShoppingCartDoesNotContainProductException::new,
                NO_ORDER_DOES_NOT_CONTAIN_ITEM, id, productId);
    }

}
