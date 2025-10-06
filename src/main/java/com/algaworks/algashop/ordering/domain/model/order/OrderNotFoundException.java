package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.exception.DomainException;
import io.hypersistence.tsid.TSID;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.NO_ORDER_NOT_FOUND;

public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public static OrderNotFoundException because(TSID orderId) {
        return of(OrderNotFoundException::new, NO_ORDER_NOT_FOUND, orderId);
    }

}
