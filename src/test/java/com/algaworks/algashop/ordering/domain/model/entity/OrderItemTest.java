package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderItem;
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
