package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.entity.fixture.OrderTextFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIsCanceledTest {

    @Test
    void givenOrderWithStatusCanceled_whenIsCanceled_shouldReturnTrue() {
        var order = OrderTextFixture.anOrder().status(OrderStatusEnum.CANCELED).build();
        assertThat(order.isCanceled()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatusEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"CANCELED", "DRAFT"})
    void givenOrderWithNonCanceledStatus_whenIsCanceled_shouldReturnFalse(OrderStatusEnum status) {
        var order = OrderTextFixture.anOrder().status(status).build();
        assertThat(order.isCanceled()).isFalse();
    }

}
