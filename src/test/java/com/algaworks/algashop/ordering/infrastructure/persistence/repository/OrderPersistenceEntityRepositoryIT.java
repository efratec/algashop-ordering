package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.domain.model.utility.GeneratorId;
import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderPersistenceEntityTestFixture;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
class OrderPersistenceEntityRepositoryIT {

    private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    @Test
    void shouldPersist() {
        long orderId = GeneratorId.gererateTSID().toLong();
        var entity = OrderPersistenceEntity.builder()
                .id(orderId)
                .customerId(GeneratorId.generateTimeBasedUUID())
                .totalItems(2)
                .totalAmount(new BigDecimal(1000))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .placedAt(OffsetDateTime.now())
                .build();

        orderPersistenceEntityRepository.saveAndFlush(entity);
        assertThat(orderPersistenceEntityRepository.existsById(orderId)).isTrue();
    }

    @Test
    void shouldCount() {
        long ordersCount = orderPersistenceEntityRepository.count();
        assertThat(ordersCount).isZero();
    }

    @Test
    void shouldSetAuditingValues() {
        var entity = OrderPersistenceEntityTestFixture.existingOrder().build();
        entity = orderPersistenceEntityRepository.saveAndFlush(entity);
        assertThat(entity.getCreatedByUserId()).isNotNull();
        assertThat(entity.getLastModifiedAt()).isNotNull();
        assertThat(entity.getLastModifiedByUserId()).isNotNull();
    }

}
