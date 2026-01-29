package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

public class BadGatewayException extends DomainException {

    public BadGatewayException() {
    }

    private BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BadGatewayException of(String message, Throwable cause) {
        throw new BadGatewayException(message, cause);
    }

}
