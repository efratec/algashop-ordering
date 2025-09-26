package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomersPersistenceProviderIT {

    private final CustomersPersistenceProvider persistenceProvider;

    @Test
    void givenANewCustomer_shouldPersistEntity() {
        var customer = CustomerTestFixture.brandNewCustomer().build();

        persistenceProvider.add(customer);

        var persitenceEntity = persistenceProvider.ofId(customer.id()).orElseThrow();

        assertThat(persitenceEntity).satisfies(entity -> {
            assertThat(entity.id()).isNotNull();
            assertThat(entity.fullName()).isEqualTo(customer.fullName());
            assertThat(entity.birthDate()).isEqualTo(customer.birthDate());
            assertThat(entity.email()).isEqualTo(customer.email());
            assertThat(entity.phone()).isEqualTo(customer.phone());
            assertThat(entity.document()).isEqualTo(customer.document());
            assertThat(entity.isPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed());
            assertThat(entity.isArchived()).isNotNull();
            assertThat(entity.registeredAt()).isNotNull();
            assertThat(entity.address()).isEqualTo(customer.address());
        });
    }

    @Test
    void givenANewCustomer_whenChangeName_shouldPersistUpdatedNameAndVersion() {
        var customer = CustomerTestFixture.brandNewCustomer().build();
        persistenceProvider.add(customer);

        var persisted = persistenceProvider.ofId(customer.id()).orElseThrow();
        var previousVersion = persisted.version();

        var newName = FullName.of("Lula Ladrao", "da Silva");
        persisted.changeName(newName);
        persistenceProvider.add(persisted);

        var reloaded = persistenceProvider.ofId(customer.id()).orElseThrow();

        assertThat(reloaded).satisfies(entity -> {
            assertThat(entity.id()).isNotNull();
            assertThat(entity.fullName()).isEqualTo(newName);
            assertThat(entity.birthDate()).isEqualTo(customer.birthDate());
            assertThat(entity.email()).isEqualTo(customer.email());
            assertThat(entity.phone()).isEqualTo(customer.phone());
            assertThat(entity.document()).isEqualTo(customer.document());
            assertThat(entity.isPromotionNotificationsAllowed())
                    .isEqualTo(customer.isPromotionNotificationsAllowed());
            assertThat(entity.isArchived()).isNotNull();
            assertThat(entity.registeredAt()).isNotNull();
            assertThat(entity.address()).isEqualTo(customer.address());
            assertThat(entity.version()).isGreaterThan(previousVersion);
        });
    }

}
