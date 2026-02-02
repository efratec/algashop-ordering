package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrdersPersistenceProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Year;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@Import({OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssembler.class,
        OrderPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrdersIT {

    private final Customers customers;
    private final Orders orders;

    @BeforeEach
    void setup() {
        if (!customers.exists(DEFAULT_CUSTOMER_ID)) {
            customers.add(existingCustomer().build());
        }
    }

    @Test
    void shouldPersistAndFind() {
        var originalOrder = OrderTestFixture.anOrder().build();
        var orderId = originalOrder.id();
        orders.add(originalOrder);

        var possibleOrder = orders.ofId(orderId);
        assertThat(possibleOrder).isPresent();

        var savedOrder = possibleOrder.get();

        assertThat(savedOrder).satisfies(
                s -> assertThat(s.id()).isEqualTo(orderId),
                s -> assertThat(s.customerId()).isEqualTo(originalOrder.customerId()),
                s -> assertThat(s.totalAmount()).isEqualTo(originalOrder.totalAmount()),
                s -> assertThat(s.totalItems()).isEqualTo(originalOrder.totalItems()),
                s -> assertThat(s.placedAt()).isEqualTo(originalOrder.placedAt()),
                s -> assertThat(s.paidAt()).isEqualTo(originalOrder.paidAt()),
                s -> assertThat(s.canceledAt()).isEqualTo(originalOrder.canceledAt()),
                s -> assertThat(s.readyAt()).isEqualTo(originalOrder.readyAt()),
                s -> assertThat(s.status()).isEqualTo(originalOrder.status()),
                s -> assertThat(s.paymentMethod()).isEqualTo(originalOrder.paymentMethod())
        );
    }

    @Test
    void shouldUpdateExistingOrder() {
        var order = OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).build();
        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();
        order.markAsPaid();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        assertThat(order.isPaid()).isTrue();
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        var order = OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).build();
        orders.add(order);

        var orderT1 = orders.ofId(order.id()).orElseThrow();
        var orderT2 = orders.ofId(order.id()).orElseThrow();

        orderT1.markAsPaid();
        orders.add(orderT1);

        orderT2.cancel();

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> orders.add(orderT2));

        var savedOrder = orders.ofId(order.id()).orElseThrow();
        assertThat(savedOrder.canceledAt()).isNull();
        assertThat(savedOrder.paidAt()).isNotNull();
    }

    @Test
    void shouldCountExistingOrders() {
        assertThat(orders.count()).isZero();

        var order1 = OrderTestFixture.anOrder().build();
        var order2 = OrderTestFixture.anOrder().build();

        orders.add(order1);
        orders.add(order2);

        assertThat(orders.count()).isEqualTo(2L);
    }

    @Test
    void shouldReturnIfOrderExists() {
        var order = OrderTestFixture.anOrder().build();

        orders.add(order);

        assertThat(orders.exists(order.id())).isTrue();
        assertThat(orders.exists(OrderId.of())).isFalse();
    }

    @Test
    void shouldListExistingOrdersByYear() {
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.DRAFT).build());

        var customerId = DEFAULT_CUSTOMER_ID;

        var ordersList = orders.placedByCustomerInYear(customerId, Year.now());
        assertThat(ordersList).hasSize(2);

        ordersList = orders.placedByCustomerInYear(customerId, Year.now().minusYears(1));
        assertThat(ordersList).isEmpty();

        ordersList = orders.placedByCustomerInYear(CustomerId.of(), Year.now());
        assertThat(ordersList).isEmpty();
    }

    @Test
    void shouldReturnTotalSoldByCustomer() {
        var orderT1 = OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).build();
        var orderT2 = OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).build();
        orders.add(orderT1);
        orders.add(orderT2);

        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).build());

        Money expectedTotalAmount = orderT1.totalAmount().add(orderT2.totalAmount());

        assertThat(orders.totalSoldForCustomer(DEFAULT_CUSTOMER_ID)).isEqualTo(expectedTotalAmount);
        assertThat(orders.totalSoldForCustomer(CustomerId.of())).isEqualTo(Money.ZERO());
    }

    @Test
    void shouldReturnSalesQuantityByCustomerInYear() {
        var orderT1 = OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).build();
        var orderT2 = OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).build();
        orders.add(orderT1);
        orders.add(orderT2);

        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).build());
        orders.add(OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).build());

        var customerId = DEFAULT_CUSTOMER_ID;

        assertThat(orders.salesQuantityByCustomerInYear(customerId, Year.now())).isEqualTo(2);
        assertThat(orders.salesQuantityByCustomerInYear(customerId, Year.now().minusYears(1))).isZero();
    }

}