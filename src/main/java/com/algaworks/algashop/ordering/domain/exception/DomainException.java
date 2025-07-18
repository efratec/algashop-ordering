package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessage;

import java.util.function.Function;

public class DomainException extends RuntimeException {

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    protected DomainException(String message) {
        super(message);
    }

    public static <T extends DomainException> T of(
            Function<String, T> exceptionCreator,
            ReasonMessage reason,
            Object... args) {
        return exceptionCreator.apply(reason.format(args));
    }

}
