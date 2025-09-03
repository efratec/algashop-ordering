package com.algaworks.algashop.ordering.domain.model.exception;

public class CustomerEmailIsUseException extends DomainException{

    public CustomerEmailIsUseException() {
        super("Customer email is already used");
    }

    protected CustomerEmailIsUseException(String message) {
        super(message);
    }

}
