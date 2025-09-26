package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

public class OrderCannotBeEditedException extends DomainException {

    public OrderCannotBeEditedException(String message) {
        super(message);
    }

}
