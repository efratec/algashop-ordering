package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void shouldGenerate() {
        Assertions.assertNotNull(OrderItem.brandNew()
                .product(ProductTestFixture.aProduct().build())
                .quantity(Quantity.of(1))
                .orderId(OrderId.of())
                .build());
    }

}
