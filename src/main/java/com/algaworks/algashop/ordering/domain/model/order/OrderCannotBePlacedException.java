package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

public class OrderCannotBePlacedException extends DomainException {

    public OrderCannotBePlacedException(String message) {
        super(message);
    }

}
