package com.algaworks.algashop.ordering.core.domain.model.commons;

import com.algaworks.algashop.ordering.core.domain.validator.FieldValidations;

public record Email(String value) {

    public Email {
        FieldValidations.requiresValidEmail(value);
    }

    public static Email of(String value) {
        return new Email(value);
    }

}
