package com.algaworks.algashop.ordering.domain.model.exception.enums;

@FunctionalInterface
public interface ReasonMessage {
    String format(Object... args);
}
