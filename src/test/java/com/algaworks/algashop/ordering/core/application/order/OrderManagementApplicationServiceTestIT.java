package com.algaworks.algashop.ordering.core.application.order;

import com.algaworks.algashop.ordering.core.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.order.*;
import com.algaworks.algashop.ordering.core.ports.in.order.ForManagingOrders;
import com.algaworks.algashop.ordering.infrastructure.adapters.in.listener.order.OrderEventListener;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestFixture.brandNewCustomer;
import static com.algaworks.algashop.ordering.core.domain.model.order.OrderTestFixture.anOrder;
import static com.algaworks.algashop.ordering.core.domain.model.order.OrderStatusEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Import(OrderEventListener.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrderManagementApplicationServiceTestIT extends AbstractApplicationIT {

    private final Orders orders;
    private final Customers customers;
    private final ForManagingOrders forManagingOrders;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @MockitoSpyBean
    private ForAddingLoyaltyPoints loyaltyPointsApplicationService;

    @Test
    void shouldCancelOrder_whenOrderIsCancelable() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).build();
        orders.add(order);

        forManagingOrders.cancel(order.id().value().toLong());

        var orderCanceled = orders.ofId(order.id()).orElseThrow();
        assertThat(orderCanceled.isCanceled()).isTrue();
        assertThat(orderCanceled.canceledAt()).isNotNull();

        Mockito.verify(orderEventListener).listen(Mockito.any(OrderCanceledEvent.class));
    }

    @Test
    void shouldThrowOrderNotFoundException_whenIsCancelable_withNonexistentOrder() {
        var orderNotExisted = anOrder().build();
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> forManagingOrders.cancel(orderNotExisted.id().value().toLong()));
    }

    @Test
    void shouldMarkOrderAsPaid_andPersist_whenOrderExists() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(PLACED).build();
        orders.add(order);

        forManagingOrders.markAsPaid(order.id().value().toLong());

        var orderMarked = orders.ofId(order.id()).orElseThrow();
        assertThat(orderMarked.isPaid()).isTrue();
        assertThat(orderMarked.paidAt()).isNotNull();

        Mockito.verify(orderEventListener, Mockito.times(1)).listen(Mockito.any(OrderPaidEvent.class));
    }

    @Test
    void shouldThrowOrderNotFoundException_whenMarkingAsPaid_withNonexistentOrder() {
        var orderNotExisted = anOrder().build();
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> forManagingOrders.markAsPaid(orderNotExisted.id().value().toLong()));
    }

    @Test
    void shouldMarkOrderAsReady_andPersist_whenOrderExists() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(PAID).build();
        orders.add(order);

        forManagingOrders.markAsReady(order.id().value().toLong());

        var orderMarked = orders.ofId(order.id()).orElseThrow();
        assertThat(orderMarked.isReady()).isTrue();
        assertThat(orderMarked.readyAt()).isNotNull();

        Mockito.verify(orderEventListener,Mockito.times(1)).listen(Mockito.any(OrderReadyEvent.class));
        Mockito.verify(loyaltyPointsApplicationService).addLoyaltyPoints(Mockito.any(UUID.class), Mockito.any(String.class));
    }

    @Test
    void shouldThrowOrderNotFoundException_whenMarkingAsReady_withNonexistentOrder() {
        var orderNotExisted = anOrder().build();
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> forManagingOrders.markAsReady(orderNotExisted.id().value().toLong()));
    }

    @Test
    void shouldThrowStatusCannotBeChangedException_whenOrderInInvalidStatus() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(CANCELED).build();
        orders.add(order);

        var orderCanceled = orders.ofId(order.id()).orElseThrow();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> forManagingOrders.cancel(orderCanceled.id().value().toLong()));
    }

    @Test
    void shouldThrowStatusCannotBeChangedException_whenIsMarkPaidOrderInInvalidStatus() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(PAID).build();
        orders.add(order);

        var orderPaid = orders.ofId(order.id()).orElseThrow();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> forManagingOrders.markAsPaid(orderPaid.id().value().toLong()));
    }

    @Test
    void shouldThrowStatusCannotBeChangedException_whenIsMarkReadyOrderInInvalidStatus() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(READY).build();
        orders.add(order);

        var orderReady = orders.ofId(order.id()).orElseThrow();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> forManagingOrders.markAsReady(orderReady.id().value().toLong()));
    }

}