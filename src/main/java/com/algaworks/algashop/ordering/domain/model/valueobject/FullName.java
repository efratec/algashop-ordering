package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record FullName(String firstName, String lastName) {

    public FullName {
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);

        if (firstName.isBlank() || lastName.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    public static FullName of(String firstName, String lastName) {
        return new FullName(firstName, lastName);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}
