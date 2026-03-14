package com.algaworks.algashop.ordering.core.domain.model.customer;

import com.algaworks.algashop.ordering.core.domain.model.order.OrderStatusEnum;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderTestFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerLoyaltPointsServiceTest {

    private final CustomerLoyaltPointsService service = new CustomerLoyaltPointsService();

    @Test
    void givenValidCustomerAndOrder_WhenAddingPoints_ShouldAccumulate() {
        var customer = CustomerTestFixture.existingCustomer().build();
        var order = OrderTestFixture.anOrder().status(OrderStatusEnum.READY).build();

        service.addPoints(customer, order);
        assertThat(customer.loyaltyPoints()).isEqualTo(LoyaltyPoints.of(30));
    }

}