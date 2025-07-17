package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.entity.fixture.ProductTestFixture;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void shouldGenerate() {
        Assertions.assertNotNull(OrderItem.brandNew()
                .product(ProductTestFixture.aProduct().build())
                .quantity(Quantity.of(1))
                .orderId(OrderId.generate())
                .build());
    }

}
