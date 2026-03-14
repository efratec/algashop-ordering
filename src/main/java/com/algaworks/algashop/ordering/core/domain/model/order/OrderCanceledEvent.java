package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;

import java.time.OffsetDateTime;

public record OrderCanceledEvent(OrderId orderId, CustomerId customerId, OffsetDateTime canceledAt) {

    public static OrderCanceledEvent of(OrderId orderId, CustomerId customerId, OffsetDateTime canceledAt) {
        return new OrderCanceledEvent(orderId, customerId, canceledAt);
    }

}
