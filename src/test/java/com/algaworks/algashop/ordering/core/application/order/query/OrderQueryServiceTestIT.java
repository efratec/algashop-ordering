package com.algaworks.algashop.ordering.core.application.order.query;

import com.algaworks.algashop.ordering.core.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.core.application.order.OrderQueryService;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.entity.fixture.CustomerTestFixture;
import com.algaworks.algashop.ordering.core.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderStatusEnum;
import com.algaworks.algashop.ordering.core.domain.model.order.Orders;
import com.algaworks.algashop.ordering.core.ports.in.order.OrderFilter;
import com.algaworks.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.algaworks.algashop.ordering.core.ports.out.order.OrderSummaryOutput;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrderQueryServiceTestIT extends AbstractApplicationIT {

    private final OrderQueryService queryService;
    private final Orders orders;
    private final Customers customers;

    @Test
    public void shouldFindById() {
        var customer = CustomerTestFixture.existingCustomer().build();
        customers.add(customer);

        var order = OrderTestFixture.anOrder().customerId(customer.id()).build();
        orders.add(order);

        OrderDetailOutput output = queryService.findById(order.id().value().toString());

        assertThat(output)
                .extracting(
                        OrderDetailOutput::getId,
                        OrderDetailOutput::getTotalAmount
                )
                .containsExactly(
                        order.id(),
                        order.totalAmount().value()
                );
    }

    @Test
    public void shouldFilterByPage() {
        var customer = CustomerTestFixture.existingCustomer().build();
        customers.add(customer);

        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.DRAFT).withItems(false).customerId(customer.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).customerId(customer.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).customerId(customer.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.READY).customerId(customer.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).customerId(customer.id()).build());

        Page<OrderSummaryOutput> page = queryService.filter(OrderFilter.pagination(3, 0));

        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    public void shouldFilterByCustomerId() {
        var customer1 = CustomerTestFixture.existingCustomer().build();
        customers.add(customer1);

        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.DRAFT).withItems(false).customerId(customer1.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).customerId(customer1.id()).build());

        var customer2 = CustomerTestFixture.existingCustomer().id(CustomerId.of()).build();
        customers.add(customer2);
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).customerId(customer2.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.READY).customerId(customer2.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).customerId(customer2.id()).build());

        OrderFilter filter = OrderFilter.builder().build();
        filter.setCustomerId(customer1.id().value());

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByMultipleParams() {
        var customer1 = CustomerTestFixture.existingCustomer().build();
        customers.add(customer1);

        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.DRAFT).withItems(false).customerId(customer1.id()).build());
        var order1 = OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).customerId(customer1.id()).build();
        orders.add(order1);

        var customer2 = CustomerTestFixture.existingCustomer().id(CustomerId.of()).build();
        customers.add(customer2);
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).customerId(customer2.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.READY).customerId(customer2.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).customerId(customer2.id()).build());

        var orderFilter = OrderFilter.builder().build();
        orderFilter.setCustomerId(customer1.id().value());
        orderFilter.setStatus(OrderStatusEnum.PLACED.toString().toLowerCase());
        orderFilter.setTotalAmountFrom(order1.totalAmount().value());

        Page<OrderSummaryOutput> page = queryService.filter(orderFilter);

        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void givenInvalidOrderId_whenFilter_shouldReturnEmptyPage() {
        var customer1 = CustomerTestFixture.existingCustomer().build();
        customers.add(customer1);

        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.DRAFT).withItems(false).customerId(customer1.id()).build());
        var order1 = OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).customerId(customer1.id()).build();
        orders.add(order1);

        var customer2 = CustomerTestFixture.existingCustomer().id(CustomerId.of()).build();
        customers.add(customer2);
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).customerId(customer2.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.READY).customerId(customer2.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).customerId(customer2.id()).build());

        OrderFilter filter = OrderFilter.builder().build();
        filter.setOrderId("ABC");

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(0);
        assertThat(page.getTotalElements()).isEqualTo(0);
        assertThat(page.getNumberOfElements()).isEqualTo(0);
    }

    @Test
    public void shouldOrderByStatus() {
        var customer1 = CustomerTestFixture.existingCustomer().build();
        customers.add(customer1);

        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.DRAFT).withItems(false).customerId(customer1.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).customerId(customer1.id()).build());

        var customer2 = CustomerTestFixture.existingCustomer().id(CustomerId.of()).build();
        customers.add(customer2);
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).customerId(customer2.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.READY).customerId(customer2.id()).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).customerId(customer2.id()).build());

        OrderFilter filter = OrderFilter.builder().build();
        filter.setSortByProperty(OrderFilter.SortType.STATUS);
        filter.setSortDirection(Sort.Direction.ASC);

        Page<OrderSummaryOutput> page = queryService.filter(filter);

        assertThat(page.getContent().getFirst().getStatus()).isEqualTo(OrderStatusEnum.CANCELED.toString());
    }

}