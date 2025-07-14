package com.algaworks.algashop.ordering.domain.valueobject;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public record BirthDate(LocalDate value) {

    public BirthDate {
        Objects.requireNonNull(value);
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException();
        }
    }

    public static BirthDate of(LocalDate value) {
        return new BirthDate(value);
    }

    public Integer getAge() {
        return Period.between(this.value, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}
