package com.algaworks.algashop.ordering.domain.model.entity.fixture;

import com.algaworks.algashop.ordering.domain.model.utility.GeneratorId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity.OrderPersistenceEntityBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public class OrderPersistenceEntityTestFixture {

    private OrderPersistenceEntityTestFixture() {
    }

    public static OrderPersistenceEntityBuilder existingOrder() {
        return OrderPersistenceEntity.builder()
                .id(GeneratorId.gererateTSID().toLong())
                .customerId(GeneratorId.generateTimeBasedUUID())
                .totalItems(3)
                .totalAmount(new BigDecimal(1000))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .placedAt(OffsetDateTime.now())
                .items(Set.of(existingOrderItem().build(), existingItemAlt().build()));
    }

    public static OrderItemPersistenceEntity.OrderItemPersistenceEntityBuilder existingOrderItem() {
        return OrderItemPersistenceEntity.builder()
                .id(GeneratorId.gererateTSID().toLong())
                .price(new BigDecimal(500))
                .quantity(2)
                .totalAmount(new BigDecimal(1000))
                .productName("Notebook")
                .productId(GeneratorId.generateTimeBasedUUID());
    }

    public static OrderItemPersistenceEntity.OrderItemPersistenceEntityBuilder existingItemAlt(){
        return OrderItemPersistenceEntity.builder()
                .id(GeneratorId.gererateTSID().toLong())
                .price(new BigDecimal(250))
                .quantity(1)
                .totalAmount(new BigDecimal(250))
                .productName("Mouse pad")
                .productId(GeneratorId.generateTimeBasedUUID());
    }

}
