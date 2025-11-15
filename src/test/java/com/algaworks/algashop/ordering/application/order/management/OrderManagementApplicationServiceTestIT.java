package com.algaworks.algashop.ordering.application.order.management;

import com.algaworks.algashop.ordering.application.order.notification.OrderNotificationApplicationService;
import com.algaworks.algashop.ordering.application.order.notification.OrderNotificationApplicationService.NotifyNewRegistrationInput;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.brandNewCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture.anOrder;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
@Import(OrderEventListener.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrderManagementApplicationServiceTestIT {

    private final Orders orders;
    private final Customers customers;
    private final OrderManagementApplicationService orderManagementApplicationService;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @MockitoSpyBean
    private OrderNotificationApplicationService orderNotificationApplicationService;

    @Test
    void shouldCancelOrder_whenOrderIsCancelable() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).build();
        orders.add(order);

        orderManagementApplicationService.cancel(order.id().value().toLong());

        var orderCanceled = orders.ofId(order.id()).orElseThrow();
        assertThat(orderCanceled.isCanceled()).isTrue();
        assertThat(orderCanceled.canceledAt()).isNotNull();

        Mockito.verify(orderEventListener).listen(Mockito.any(OrderCanceledEvent.class));
        Mockito.verify(orderNotificationApplicationService).notifyNewRegistration(
                Mockito.any(NotifyNewRegistrationInput.class));

    }

    @Test
    void shouldThrowOrderNotFoundException_whenIsCancelable_withNonexistentOrder() {
        var orderNotExisted = anOrder().build();
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> orderManagementApplicationService.cancel(orderNotExisted.id().value().toLong()));
    }

    @Test
    void shouldMarkOrderAsPaid_andPersist_whenOrderExists() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(PLACED).build();
        orders.add(order);

        orderManagementApplicationService.markAsPaid(order.id().value().toLong());

        var orderMarked = orders.ofId(order.id()).orElseThrow();
        assertThat(orderMarked.isPaid()).isTrue();
        assertThat(orderMarked.paidAt()).isNotNull();

        Mockito.verify(orderEventListener, Mockito.times(1)).listen(Mockito.any(OrderPaidEvent.class));
        Mockito.verify(orderNotificationApplicationService, Mockito.times(2)).notifyNewRegistration(
                Mockito.any(NotifyNewRegistrationInput.class));
    }

    @Test
    void shouldThrowOrderNotFoundException_whenMarkingAsPaid_withNonexistentOrder() {
        var orderNotExisted = anOrder().build();
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> orderManagementApplicationService.markAsPaid(orderNotExisted.id().value().toLong()));
    }

    @Test
    void shouldMarkOrderAsReady_andPersist_whenOrderExists() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(PAID).build();
        orders.add(order);

        orderManagementApplicationService.markAsReady(order.id().value().toLong());

        var orderMarked = orders.ofId(order.id()).orElseThrow();
        assertThat(orderMarked.isReady()).isTrue();
        assertThat(orderMarked.readyAt()).isNotNull();

        Mockito.verify(orderEventListener,Mockito.times(1)).listen(Mockito.any(OrderReadyEvent.class));
        Mockito.verify(orderNotificationApplicationService, Mockito.times(3)).notifyNewRegistration(
                Mockito.any(OrderNotificationApplicationService.NotifyNewRegistrationInput.class));
    }

    @Test
    void shouldThrowOrderNotFoundException_whenMarkingAsReady_withNonexistentOrder() {
        var orderNotExisted = anOrder().build();
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> orderManagementApplicationService.markAsReady(orderNotExisted.id().value().toLong()));
    }

    @Test
    void shouldThrowStatusCannotBeChangedException_whenOrderInInvalidStatus() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(CANCELED).build();
        orders.add(order);

        var orderCanceled = orders.ofId(order.id()).orElseThrow();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> orderManagementApplicationService.cancel(orderCanceled.id().value().toLong()));
    }

    @Test
    void shouldThrowStatusCannotBeChangedException_whenIsMarkPaidOrderInInvalidStatus() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(PAID).build();
        orders.add(order);

        var orderPaid = orders.ofId(order.id()).orElseThrow();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> orderManagementApplicationService.markAsPaid(orderPaid.id().value().toLong()));
    }

    @Test
    void shouldThrowStatusCannotBeChangedException_whenIsMarkReadyOrderInInvalidStatus() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var order = anOrder().customerId(customer.id()).status(READY).build();
        orders.add(order);

        var orderReady = orders.ofId(order.id()).orElseThrow();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> orderManagementApplicationService.markAsReady(orderReady.id().value().toLong()));
    }

}