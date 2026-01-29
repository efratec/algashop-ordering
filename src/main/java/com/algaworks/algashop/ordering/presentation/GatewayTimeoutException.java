package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

public class GatewayTimeoutException extends DomainException {

    public GatewayTimeoutException() {
    }

    private GatewayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public static GatewayTimeoutException of(String message, Throwable cause) {
        throw new GatewayTimeoutException(message, cause);
    }

}
