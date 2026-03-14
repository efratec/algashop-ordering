package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.exception.DomainException;

public class OrderCannotBePlacedException extends DomainException {

    public OrderCannotBePlacedException(String message) {
        super(message);
    }

}
