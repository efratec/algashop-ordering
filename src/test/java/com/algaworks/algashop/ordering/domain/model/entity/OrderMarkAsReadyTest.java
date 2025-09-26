package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PAID;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.READY;
import static org.assertj.core.api.Assertions.*;

class OrderMarkAsReadyTest {

    @Test
    void givenOrderPaid_whenMarkAsReady_shouldChangedStatus() {
        var order = OrderTestFixture.anOrder().status(PAID).build();
        order.markAsReady();
        assertWith(order,
                o -> assertThat(o.readyAt()).isNotNull(),
                o -> assertThat(o.isReady()).isTrue(),
                o -> assertThat(o.status()).isEqualTo(READY));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatusEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"PAID", "READY", "CANCELED"})
    void givenOrderWithNonReadStatus_whenMarkAsReady_shouldNotChangedStatusAndThrowException(OrderStatusEnum status) {
        var order = OrderTestFixture.anOrder().status(status).build();

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::markAsReady);

        assertWith(order,
                o -> Assertions.assertThat(o.status()).isEqualTo(status),
                o -> Assertions.assertThat(o.readyAt()).isNull());
    }

    @Test
    void givenPaidOrder_whenMarkAsReady_shouldChangeStatusToReadyAndSetReadyAt() {
        var order = OrderTestFixture.anOrder().status(OrderStatusEnum.PAID).build();

        order.markAsReady();

        assertWith(order,
                o -> assertThat(o.status()).isEqualTo(OrderStatusEnum.READY),
                o -> assertThat(o.readyAt()).isNotNull());
    }

}
