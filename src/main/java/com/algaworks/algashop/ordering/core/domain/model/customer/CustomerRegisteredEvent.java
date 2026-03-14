package com.algaworks.algashop.ordering.core.domain.model.customer;

import com.algaworks.algashop.ordering.core.domain.model.commons.Email;
import com.algaworks.algashop.ordering.core.domain.model.commons.FullName;

import java.time.OffsetDateTime;

public record CustomerRegisteredEvent(CustomerId customerId,
                                      OffsetDateTime registeredAt,
                                      FullName fullName,
                                      Email email) {

    public static CustomerRegisteredEvent of(CustomerId customerId, OffsetDateTime registeredAt,
                                             FullName fullName, Email email) {
        return new CustomerRegisteredEvent(customerId, registeredAt, fullName, email);
    }

}
