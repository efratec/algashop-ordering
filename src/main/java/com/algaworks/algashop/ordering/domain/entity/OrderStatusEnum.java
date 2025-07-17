package com.algaworks.algashop.ordering.domain.entity;

import java.util.List;

public enum OrderStatusEnum {

    DRAFT,
    PLACED(DRAFT),
    PAID(PLACED),
    READY(PAID),
    CANCELED(PAID, READY, PLACED, DRAFT);

    OrderStatusEnum(OrderStatusEnum... previousStatus) {
        this.previousStatus = List.of(previousStatus);
    }

    private final List<OrderStatusEnum> previousStatus;

    public boolean canChangeTo(OrderStatusEnum newStatus) {
        var currentStatus = this;
        return newStatus.previousStatus.contains(currentStatus);
    }

    public boolean canNotChangeTo(OrderStatusEnum newStatus) {
        return !canChangeTo(newStatus);
    }

}
