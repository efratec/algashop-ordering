package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;

import static com.algaworks.algashop.ordering.domain.model.exception.enums.OrderReason.NO_ORDER_DOES_NOT_CONTAIN_ITEM;

public class OrderDoesNotContainOrderItemException extends DomainException {

    public OrderDoesNotContainOrderItemException(String message) {
        super(message);
    }

    public static OrderDoesNotContainOrderItemException because(OrderId id, OrderItemId itemId) {
        return DomainException.of(OrderDoesNotContainOrderItemException::new,
                NO_ORDER_DOES_NOT_CONTAIN_ITEM, id, itemId);
    }

}
