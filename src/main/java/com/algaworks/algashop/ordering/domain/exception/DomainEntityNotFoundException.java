package com.algaworks.algashop.ordering.domain.exception;

public class DomainEntityNotFoundException extends DomainException {

    public DomainEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainEntityNotFoundException(String message) {
        super(message);
    }

}
