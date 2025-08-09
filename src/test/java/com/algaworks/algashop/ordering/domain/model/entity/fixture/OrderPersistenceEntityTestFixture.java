package com.algaworks.algashop.ordering.domain.model.entity.fixture;

import com.algaworks.algashop.ordering.domain.model.utility.GeneratorId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity.OrderPersistenceEntityBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderPersistenceEntityTestFixture {

    private OrderPersistenceEntityTestFixture() {
    }

    public static OrderPersistenceEntityBuilder existingOrder() {
        return OrderPersistenceEntity.builder()
                .id(GeneratorId.gererateTSID().toLong())
                .customerId(GeneratorId.generateTimeBasedUUID())
                .totalItems(2)
                .totalAmount(new BigDecimal(1000))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .placedAt(OffsetDateTime.now());
    }

}
