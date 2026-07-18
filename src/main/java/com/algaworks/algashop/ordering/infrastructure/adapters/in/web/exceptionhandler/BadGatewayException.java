package com.algaworks.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler;

import com.algaworks.algashop.ordering.core.domain.exception.DomainException;

public class BadGatewayException extends DomainException {

    public BadGatewayException() {
    }

    public BadGatewayException(String message) {
        super(message);
    }

    public BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BadGatewayException of(String message, Throwable cause) {
        throw new BadGatewayException(message, cause);
    }

    public static class ServerErrorException extends BadGatewayException {
        public ServerErrorException() {
        }

        public ServerErrorException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ClientErrorException extends BadGatewayException {
        public ClientErrorException() {
        }

        public ClientErrorException(String message) {
            super(message);
        }

        public ClientErrorException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
