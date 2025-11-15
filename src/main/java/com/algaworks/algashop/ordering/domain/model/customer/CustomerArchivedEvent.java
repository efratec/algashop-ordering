package com.algaworks.algashop.ordering.domain.model.customer;

import java.time.OffsetDateTime;

public record CustomerArchivedEvent(CustomerId customerId, OffsetDateTime archivedAt) {

    public static CustomerArchivedEvent of(CustomerId customerId, OffsetDateTime archivedAt) {
        return new CustomerArchivedEvent(customerId, archivedAt);
    }

}
