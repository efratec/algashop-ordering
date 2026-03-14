package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.infrastructure.persistence.AbstractPersistenceIT;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestFixture;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestFixture;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.algaworks.algashop.ordering.core.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrderPersistenceEntityRepositoryIT extends AbstractPersistenceIT {

    private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;
    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    private CustomerPersistenceEntity customerPersistenceEntity;

    @BeforeEach
    void setup() {
        var customerId = DEFAULT_CUSTOMER_ID.value();
        if (!customerPersistenceEntityRepository.existsById(customerId)) {
            customerPersistenceEntity = customerPersistenceEntityRepository.saveAndFlush(
                    CustomerPersistenceEntityTestFixture.existingCustomer().build()
            );
        }
    }

    @Test
    void shouldPersist() {
        var entity = OrderPersistenceEntityTestFixture.existingOrder().customer(customerPersistenceEntity).build();

        orderPersistenceEntityRepository.saveAndFlush(entity);
        assertThat(orderPersistenceEntityRepository.existsById(entity.getId())).isTrue();

        var savedEntity = orderPersistenceEntityRepository.findById(entity.getId()).orElseThrow();
        assertThat(savedEntity.getItems()).isNotEmpty();
    }

    @Test
    void shouldCount() {
        long ordersCount = orderPersistenceEntityRepository.count();
        assertThat(ordersCount).isZero();
    }

    @Test
    void shouldSetAuditingValues() {
        var entity = OrderPersistenceEntityTestFixture.existingOrder().customer(customerPersistenceEntity).build();
        entity = orderPersistenceEntityRepository.saveAndFlush(entity);
        assertThat(entity.getCreatedByUserId()).isNotNull();
        assertThat(entity.getLastModifiedAt()).isNotNull();
        assertThat(entity.getLastModifiedByUserId()).isNotNull();
    }

}
