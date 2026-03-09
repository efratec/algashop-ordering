package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;

import java.util.Objects;
import java.util.UUID;

public record CreditCardId(UUID id) {

    public CreditCardId() {
        this(GeneratorId.generateTimeBasedUUID());
    }

    public CreditCardId {
        Objects.requireNonNull(id);
    }

}
