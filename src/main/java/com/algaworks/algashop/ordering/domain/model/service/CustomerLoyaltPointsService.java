package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.exception.CanAddLoyaltyPointsOrderIsNotReady;
import com.algaworks.algashop.ordering.domain.model.exception.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;

import static com.algaworks.algashop.ordering.domain.model.exception.enums.OrderReason.NO_ORDER_NOT_BELONGS_TO_CUSTOMER;
import static com.algaworks.algashop.ordering.domain.model.validator.FieldValidations.requireAllNonNull;
import static com.algaworks.algashop.ordering.domain.model.validator.FieldValidations.validate;

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
