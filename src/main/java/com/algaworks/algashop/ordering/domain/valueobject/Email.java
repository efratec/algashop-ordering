package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.validator.FieldValidations;

public record Email(String value) {

    public Email {
        FieldValidations.requiresValidEmail(value);
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return this.value;
    }

}
