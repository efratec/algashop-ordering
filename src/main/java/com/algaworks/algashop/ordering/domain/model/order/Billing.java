package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import lombok.Builder;

import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.requireAllNonNull;

public record Billing(FullName fullName,
                      Document document,
                      Phone phone,
                      Email email,
                      Address address) {

    @Builder(toBuilder = true)
    public Billing {
        requireAllNonNull("Fullname", fullName, "Document", document, "Phone", phone, "Email", email, "Address", address);
    }

    public static Billing of(FullName fullName, Document document, Phone phone, Email email, Address address) {
        return new Billing(fullName, document, phone, email, address);
    }

}
