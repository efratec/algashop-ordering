package com.algaworks.algashop.ordering.domain.exception;

public class OrderDoesNotContainOrderItemException extends DomainException {

    public OrderDoesNotContainOrderItemException(String message) {
        super(message);
    }

}
