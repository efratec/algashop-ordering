package com.algaworks.algashop.ordering.domain.model.exception;

public class OrderStatusCannotBeChangedException extends DomainException {

    public OrderStatusCannotBeChangedException(String message) {
        super(message);
    }

}
