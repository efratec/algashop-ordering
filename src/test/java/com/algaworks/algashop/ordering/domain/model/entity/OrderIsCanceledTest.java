package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIsCanceledTest {

    @Test
    void givenOrderWithStatusCanceled_whenIsCanceled_shouldReturnTrue() {
        var order = OrderTestFixture.anOrder().status(OrderStatusEnum.CANCELED).build();
        assertThat(order.isCanceled()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatusEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"CANCELED", "DRAFT"})
    void givenOrderWithNonCanceledStatus_whenIsCanceled_shouldReturnFalse(OrderStatusEnum status) {
        var order = OrderTestFixture.anOrder().status(status).build();
        assertThat(order.isCanceled()).isFalse();
    }

}
