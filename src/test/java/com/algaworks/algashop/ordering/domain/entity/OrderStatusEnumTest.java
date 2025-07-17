package com.algaworks.algashop.ordering.domain.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderStatusEnumTest {

    @Test
    void canChangeTo() {
        Assertions.assertThat(OrderStatusEnum.DRAFT.canChangeTo(OrderStatusEnum.PLACED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.DRAFT.canChangeTo(OrderStatusEnum.CANCELED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.PAID.canChangeTo(OrderStatusEnum.DRAFT)).isFalse();
    }

    @Test
    void canNotChangeTo() {
        Assertions.assertThat(OrderStatusEnum.PLACED.canNotChangeTo(OrderStatusEnum.DRAFT)).isTrue();
    }

}
