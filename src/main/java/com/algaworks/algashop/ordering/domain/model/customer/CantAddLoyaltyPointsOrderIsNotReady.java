package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.exception.DomainException;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.ORDER_IS_NOT_READY;

public class CantAddLoyaltyPointsOrderIsNotReady extends DomainException {

    public CantAddLoyaltyPointsOrderIsNotReady(String message) {
        super(message);
    }

    public static OrderNotFoundException because() {
        return of(OrderNotFoundException::new, ORDER_IS_NOT_READY);
    }

}
