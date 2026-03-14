package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.product.ProductTestFixture;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
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
