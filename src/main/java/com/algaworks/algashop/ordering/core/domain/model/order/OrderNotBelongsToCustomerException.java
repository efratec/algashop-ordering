package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.exception.DomainException;

public class OrderNotBelongsToCustomerException extends DomainException {

    public OrderNotBelongsToCustomerException(String message) {
        super(message);
    }
}
