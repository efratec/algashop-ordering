package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.NO_ORDER_DOES_NOT_CONTAIN_ITEM;

public class OrderDoesNotContainOrderItemException extends DomainException {

    public OrderDoesNotContainOrderItemException(String message) {
        super(message);
    }

    public static OrderDoesNotContainOrderItemException because(OrderId id, OrderItemId itemId) {
        return of(OrderDoesNotContainOrderItemException::new,
                NO_ORDER_DOES_NOT_CONTAIN_ITEM, id, itemId);
    }

}
