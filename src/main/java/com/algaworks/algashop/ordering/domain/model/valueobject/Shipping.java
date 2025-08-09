package com.algaworks.algashop.ordering.domain.model.valueobject;

import lombok.Builder;

import java.time.LocalDate;

import static com.algaworks.algashop.ordering.domain.model.validator.FieldValidations.requireAllNonNull;

@Builder
public record Shipping(
        Money cost,
        LocalDate expectedDate,
        Recipient recipient,
        Address address) {

    public Shipping {
        requireAllNonNull("Cost", cost, "ExpectedDate", expectedDate,
                "Recipient", recipient, "Address", address);
    }

    public static Shipping of(Money cost,
                              LocalDate expectedDate,
                              Recipient recipient,
                              Address address) {
        return new Shipping(cost, expectedDate, recipient, address);
    }

}
