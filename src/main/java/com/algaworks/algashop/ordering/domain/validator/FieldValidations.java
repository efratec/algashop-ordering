package com.algaworks.algashop.ordering.domain.validator;

import com.algaworks.algashop.ordering.domain.exception.DomainException;
import com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessage;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class FieldValidations {

    private FieldValidations() {
    }

    public static void requiredNonBlank(String value) {
        requiredNonBlank(value, "");
    }

    public static void requiredNonBlank(String value, String errorMessage) {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void requiresValidEmail(String email) {
        requiresValidEmail(email, null);
    }

    public static void requiresValidEmail(String email, String errorMessage) {
        Objects.requireNonNull(email, errorMessage);
        if (email.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void requireAllNonNull(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid argument count: must be pairs of (String, Object)");
        }

        for (int i = 0; i < keyValuePairs.length; i += 2) {
            var name = String.valueOf(keyValuePairs[i]);
            Object value = keyValuePairs[i + 1];
            if (value == null) {
                throw new NullPointerException(name + " must not be null");
            }
        }

    }

    public static <T extends DomainException> void validate(
            BooleanSupplier condition,
            ReasonMessage reason,
            Function<String, T> exceptionFactory,
            Object... args) {

        if (condition.getAsBoolean()) {
            throw DomainException.because(exceptionFactory, reason, args);
        }
    }

}
