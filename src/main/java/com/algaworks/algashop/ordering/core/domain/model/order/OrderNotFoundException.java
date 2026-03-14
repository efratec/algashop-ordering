package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.exception.DomainEntityNotFoundException;
import io.hypersistence.tsid.TSID;

import static com.algaworks.algashop.ordering.core.domain.exception.enums.ReasonMessageEnum.NO_ORDER_NOT_FOUND;

public class OrderNotFoundException extends DomainEntityNotFoundException {

    public OrderNotFoundException() {}

    public OrderNotFoundException(String message) {
        super(message);
    }

    public static OrderNotFoundException because(TSID orderId) {
        return of(OrderNotFoundException::new, NO_ORDER_NOT_FOUND, orderId);
    }

}
