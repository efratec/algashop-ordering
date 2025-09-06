package com.algaworks.algashop.ordering.domain.model.exception;

public class CustomerAlreadyHaveShoppingCartException extends DomainException{

    public CustomerAlreadyHaveShoppingCartException(String message) {
        super(message);
    }

}
