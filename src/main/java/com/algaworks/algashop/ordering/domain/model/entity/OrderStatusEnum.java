package com.algaworks.algashop.ordering.domain.model.entity;

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

    public void applyTransition(Order order) {
        switch (this) {
            case DRAFT -> {}
            case PLACED -> order.place();
            case PAID -> {
                order.place();
                order.markAsPaid();
            }
            case READY -> {
                order.place();
                order.markAsPaid();
                order.markAsReady();
            }
            case CANCELED -> order.cancel();
            default -> throw new IllegalStateException("Transição não suportada para status: " + this);
        }
    }

}
