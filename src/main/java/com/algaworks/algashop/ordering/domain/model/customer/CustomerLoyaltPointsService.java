package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.NO_ORDER_NOT_BELONGS_TO_CUSTOMER;
import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.requireAllNonNull;
import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.validate;

@DomainService
public class CustomerLoyaltPointsService {

    private static final LoyaltyPoints basePoints = LoyaltyPoints.of(5);
    private static final Money expectedAmountToGivePoints = Money.of("1000");

    public void addPoints(Customer customer, Order order) {
        requireAllNonNull("Customer", customer, "Order", order);

        validate(() -> (!customer.id().equals(order.customerId())), NO_ORDER_NOT_BELONGS_TO_CUSTOMER,
                OrderNotBelongsToCustomerException::new);

        if (!order.isReady()) {
            throw new CanAddLoyaltyPointsOrderIsNotReady();
        }

        customer.addLoyaltyPoints(calculatePoints(order));
    }

    private LoyaltyPoints calculatePoints(Order order) {
        if (shouldGivePointsByAmount(order.totalAmount())) {
            var result = order.totalAmount().divide(expectedAmountToGivePoints);
            return LoyaltyPoints.of(result.value().intValue() * basePoints.value());
        }
        return LoyaltyPoints.ZERO;
    }

    private boolean shouldGivePointsByAmount(Money amount) {
        return amount.compareTo(expectedAmountToGivePoints) >= 0;
    }

}
