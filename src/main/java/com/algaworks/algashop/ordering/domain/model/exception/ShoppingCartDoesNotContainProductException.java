package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

import static com.algaworks.algashop.ordering.domain.model.exception.enums.OrderReason.NO_ORDER_DOES_NOT_CONTAIN_ITEM;

public class ShoppingCartDoesNotContainProductException extends DomainException {

    public ShoppingCartDoesNotContainProductException(String message) {
        super(message);
    }

    public static ShoppingCartDoesNotContainProductException because(ShoppingCartId id, ProductId productId) {
        return DomainException.of(ShoppingCartDoesNotContainProductException::new,
                NO_ORDER_DOES_NOT_CONTAIN_ITEM, id, productId);
    }

}
