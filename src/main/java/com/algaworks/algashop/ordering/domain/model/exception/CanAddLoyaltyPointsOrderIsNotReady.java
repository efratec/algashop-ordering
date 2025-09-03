package com.algaworks.algashop.ordering.domain.model.exception;

public class CanAddLoyaltyPointsOrderIsNotReady extends DomainException {

    public CanAddLoyaltyPointsOrderIsNotReady() {
        super("Order is not ready");
    }

    public CanAddLoyaltyPointsOrderIsNotReady(String message) {
        super(message);
    }

}
