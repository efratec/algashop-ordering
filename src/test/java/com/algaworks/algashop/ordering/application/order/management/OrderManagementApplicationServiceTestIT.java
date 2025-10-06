package com.algaworks.algashop.ordering.application.order.management;

import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrderManagementApplicationServiceTestIT {

    private final Orders orders;
    private final Customers customers;
    private final OrderManagementApplicationService orderManagementApplicationService;

    @Test
    void shouldCancelOrder_whenOrderIsCancelable() {
        var customer = CustomerTestFixture.brandNewCustomer().build();
        customers.add(customer);

        var order = OrderTestFixture.anOrder().customerId(customer.id()).build();
        orders.add(order);

        orderManagementApplicationService.cancel(order.id().value().toLong());

        var orderCanceled = orders.ofId(order.id()).orElseThrow();
        assertThat(orderCanceled.isCanceled()).isTrue();
    }


}