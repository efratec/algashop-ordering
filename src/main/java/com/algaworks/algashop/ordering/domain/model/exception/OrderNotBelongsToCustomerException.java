package com.algaworks.algashop.ordering.domain.model.exception;

public class OrderNotBelongsToCustomerException extends DomainException {

    public OrderNotBelongsToCustomerException() {
        super("Order not belongs to customer");
    }

    protected OrderNotBelongsToCustomerException(String message) {
        super(message);
    }
}
