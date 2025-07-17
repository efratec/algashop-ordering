package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.requireAllNonNull;

@Builder
public record Recipient(FullName fullName, Document document, Phone phone) {

    public Recipient {
        requireAllNonNull("Fullname", fullName, "Document", document, "Phone", phone);
    }

    public static Recipient of(FullName fullName, Document document, Phone phone) {
        return new Recipient(fullName, document, phone);
    }

}
