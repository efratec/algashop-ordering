package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.entity.fixture.OrderTextFixture;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.algaworks.algashop.ordering.domain.entity.OrderStatusEnum.CANCELED;
import static org.assertj.core.api.Assertions.*;

class OrderCancelTest {

    @ParameterizedTest
    @EnumSource(value = OrderStatusEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"CANCELED"})
    void givenOrderReady_whenToCancel_shouldChangedStatus(OrderStatusEnum status) {
        var order = OrderTextFixture.anOrder().status(status).build();
        order.cancel();
        assertWith(order,
                o -> assertThat(o.canceledAt()).isNotNull(),
                o -> assertThat(o.isCanceled()).isTrue(),
                o -> assertThat(o.status()).isEqualTo(CANCELED));
    }

    @Test
    void givenOrderCancel_whenToCancel_shouldThrowException() {
        var order = OrderTextFixture.anOrder().status(CANCELED).build();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class).isThrownBy(order::cancel);
        assertWith(order,
                o -> Assertions.assertThat(o.status()).isEqualTo(CANCELED),
                o -> Assertions.assertThat(o.canceledAt()).isNotNull());
    }

}
