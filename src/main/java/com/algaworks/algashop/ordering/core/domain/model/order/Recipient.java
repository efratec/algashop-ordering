package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.commons.Document;
import com.algaworks.algashop.ordering.core.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.core.domain.model.commons.Phone;
import lombok.Builder;

import static com.algaworks.algashop.ordering.core.domain.validator.FieldValidations.requireAllNonNull;

@Builder
public record Recipient(FullName fullName, Document document, Phone phone) {

    public Recipient {
        requireAllNonNull("Fullname", fullName, "Document", document, "Phone", phone);
    }

    public static Recipient of(FullName fullName, Document document, Phone phone) {
        return new Recipient(fullName, document, phone);
    }

}
