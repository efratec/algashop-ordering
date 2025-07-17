package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.requireAllNonNull;

public record Billing(FullName fullName,
                      Document document,
                      Phone phone,
                      Address address) {

    @Builder(toBuilder = true)
    public Billing {
        requireAllNonNull("Fullname", fullName, "Document", document, "Phone", phone, "Address", address);
    }

    public static Billing of(FullName fullName, Document document, Phone phone, Address address) {
        return new Billing(fullName, document, phone, address);
    }

}
