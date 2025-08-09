package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@Import({OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssembler.class,
        OrderPersistenceEntityDisassembler.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrdersIT {

    private final Orders orders;

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

}