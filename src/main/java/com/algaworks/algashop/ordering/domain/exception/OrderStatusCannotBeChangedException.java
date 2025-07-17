package com.algaworks.algashop.ordering.domain.exception;

public class OrderStatusCannotBeChangedException extends DomainException {

    public OrderStatusCannotBeChangedException(String message) {
        super(message);
    }

}
