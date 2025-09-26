package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestFixture.existingCustomer;
import static com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntityTestFixture.existingShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
class ShoppingCartPersistenceEntityRepositoryIT {

    private final ShoppingCartPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;
    private CustomerPersistenceEntity customerPersistenceEntity;

    @BeforeEach
    void setup() {
        var customerId = DEFAULT_CUSTOMER_ID.value();
        if (!customerPersistenceEntityRepository.existsById(customerId)) {
            customerPersistenceEntity = customerPersistenceEntityRepository.saveAndFlush(existingCustomer().build());
        }
    }

    @Test
    void shouldPersistShoppingCart() {
        var shoppingCartPersistenceEntity = existingShoppingCart().customer(customerPersistenceEntity).build();

        repository.saveAndFlush(shoppingCartPersistenceEntity);

        var savedEntity = repository.findById(shoppingCartPersistenceEntity.getId()).orElseThrow();

        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getItems()).isNotEmpty();
    }

    @Test
    void shouldSetAuditingValues() {
        var entity = existingShoppingCart().customer(customerPersistenceEntity).build();
        entity = repository.saveAndFlush(entity);

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedByUserId()).isNotNull();
        assertThat(entity.getLastModifiedAt()).isNotNull();
        assertThat(entity.getLastModifiedByUserId()).isNotNull();
    }

    @Test
    void shoulCount() {
        var entity = existingShoppingCart().customer(customerPersistenceEntity).build();
        repository.saveAndFlush(entity);
        assertThat(repository.count()).isGreaterThan(0);
    }

}
