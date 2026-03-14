package com.algaworks.algashop.ordering.core.domain.exception.enums;

@FunctionalInterface
public interface ReasonMessage {
    String format(Object... args);
}
