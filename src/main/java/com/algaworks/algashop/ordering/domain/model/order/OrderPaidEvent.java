package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;

import java.time.OffsetDateTime;

public record OrderPaidEvent(OrderId orderId, CustomerId customerId, OffsetDateTime paidAt) {

    public static OrderPaidEvent of(OrderId orderId, CustomerId customerId, OffsetDateTime paidAt) {
        return new OrderPaidEvent(orderId, customerId, paidAt);
    }

}
