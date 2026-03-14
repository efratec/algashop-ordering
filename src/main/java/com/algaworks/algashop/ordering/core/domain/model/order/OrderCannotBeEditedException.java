package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.exception.DomainException;

public class OrderCannotBeEditedException extends DomainException {

    public OrderCannotBeEditedException(String message) {
        super(message);
    }

}
