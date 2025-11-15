package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;

import java.time.OffsetDateTime;

public record OrderReadyEvent(OrderId orderId, CustomerId customerId, OffsetDateTime readyAt) {

    public static OrderReadyEvent of(OrderId orderId, CustomerId customerId, OffsetDateTime readyAt) {
        return new OrderReadyEvent(orderId, customerId, readyAt);
    }

}
