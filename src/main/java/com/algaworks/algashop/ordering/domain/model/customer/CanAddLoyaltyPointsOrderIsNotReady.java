package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

public class CanAddLoyaltyPointsOrderIsNotReady extends DomainException {

    public CanAddLoyaltyPointsOrderIsNotReady() {
        super("Order is not ready");
    }

    public CanAddLoyaltyPointsOrderIsNotReady(String message) {
        super(message);
    }

}
