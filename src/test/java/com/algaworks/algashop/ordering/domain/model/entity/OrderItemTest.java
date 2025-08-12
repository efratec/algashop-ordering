package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void shouldGenerate() {
        var product = ProductTestFixture.aProduct().build();
        Assertions.assertNotNull(OrderItem.brandNew()
                .productId(product.id())
                .productName(product.name())
                .quantity(Quantity.of(1))
                .price(product.price())
                .orderId(OrderId.of())
                .build());
    }

}
