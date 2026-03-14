package com.algaworks.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler;

import com.algaworks.algashop.ordering.core.domain.exception.DomainException;

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
