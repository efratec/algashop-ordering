package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
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