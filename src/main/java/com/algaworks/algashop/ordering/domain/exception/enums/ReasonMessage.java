package com.algaworks.algashop.ordering.domain.exception.enums;

@FunctionalInterface
public interface ReasonMessage {
    String format(Object... args);
}
