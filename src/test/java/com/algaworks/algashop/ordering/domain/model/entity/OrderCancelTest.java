package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.order.OrderCanceledEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.CANCELED;
import static org.assertj.core.api.Assertions.*;

class OrderCancelTest {

    @ParameterizedTest
    @EnumSource(value = OrderStatusEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"CANCELED"})
    void givenOrderReady_whenToCancel_shouldChangedStatus(OrderStatusEnum status) {
        var order = OrderTestFixture.anOrder().status(status).build();
        order.cancel();
        assertWith(order,
                o -> assertThat(o.canceledAt()).isNotNull(),
                o -> assertThat(o.isCanceled()).isTrue(),
                o -> assertThat(o.status()).isEqualTo(CANCELED));
    }

    @Test
    void givenOrderCancel_whenToCancel_shouldThrowException() {
        var order = OrderTestFixture.anOrder().status(CANCELED).build();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class).isThrownBy(order::cancel);
        assertWith(order,
                o -> Assertions.assertThat(o.status()).isEqualTo(CANCELED),
                o -> Assertions.assertThat(o.canceledAt()).isNotNull());
    }

}
