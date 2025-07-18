package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.entity.fixture.OrderTextFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIsReadyTest {

    @Test
    void givenOrderWithStatusReady_whenIsReady_shouldReturnTrue() {
        Order order = OrderTextFixture.anOrder().status(OrderStatusEnum.READY).build();
        assertThat(order.isReady()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatusEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"READY"})
    void givenOrderWithNonReadyStatus_whenIsReady_shouldReturnFalse(OrderStatusEnum status) {
        Order order = OrderTextFixture.anOrder().status(status).build();
        assertThat(order.isReady()).isFalse();
    }

}
