package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.validator.FieldValidations;
import lombok.Builder;

import java.util.Objects;

public record Address(
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        ZipCode zipCode) {

    @Builder(toBuilder = true)
    public Address {
        FieldValidations.requiredNonBlank(street, "street should not be blank");
        FieldValidations.requiredNonBlank(number, "number should not be blank");
        FieldValidations.requiredNonBlank(neighborhood, "neighborhood should not be blank");
        FieldValidations.requiredNonBlank(city, "city should not be blank");
        FieldValidations.requiredNonBlank(state, "state should not be blank");
        Objects.requireNonNull(zipCode, "zip code should not be null");
    }

}
