package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

public class OrderStatusCannotBeChangedException extends DomainException {

    public OrderStatusCannotBeChangedException(String message) {
        super(message);
    }

}
