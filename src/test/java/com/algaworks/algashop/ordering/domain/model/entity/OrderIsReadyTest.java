package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIsReadyTest {

    @Test
    void givenOrderWithStatusReady_whenIsReady_shouldReturnTrue() {
        Order order = OrderTestFixture.anOrder().status(OrderStatusEnum.READY).build();
        assertThat(order.isReady()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatusEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"READY"})
    void givenOrderWithNonReadyStatus_whenIsReady_shouldReturnFalse(OrderStatusEnum status) {
        Order order = OrderTestFixture.anOrder().status(status).build();
        assertThat(order.isReady()).isFalse();
    }

}
