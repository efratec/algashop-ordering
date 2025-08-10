package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersitenceProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.brandNewCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@Import({CustomersPersitenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomersIT {

    private final Customers customers;

    @Test
    void shouldPersistAndFind() {
        var originalCustomer = brandNewCustomer().build();
        customers.add(originalCustomer);

        var possibleCustomer = customers.ofId(originalCustomer.id());
        assertThat(possibleCustomer).isPresent();
        assertThat(possibleCustomer.get()).satisfies(entity -> {
            assertThat(entity.id()).isNotNull();
            assertThat(entity.fullName()).isEqualTo(originalCustomer.fullName());
            assertThat(entity.birthDate()).isEqualTo(originalCustomer.birthDate());
            assertThat(entity.email()).isEqualTo(originalCustomer.email());
            assertThat(entity.phone()).isEqualTo(originalCustomer.phone());
            assertThat(entity.document()).isEqualTo(originalCustomer.document());
            assertThat(entity.isPromotionNotificationsAllowed()).isEqualTo(originalCustomer.isPromotionNotificationsAllowed());
            assertThat(entity.isArchived()).isNotNull();
            assertThat(entity.registeredAt()).isNotNull();
            assertThat(entity.address()).isEqualTo(originalCustomer.address());
        });
    }

    @Test
    void shouldUpateExistingCustomer() {
        var customer = existingCustomer().build();
        customers.add(customer);

        var newName = FullName.of("Teste", "Testando");
        var newEmail = Email.of("teste@teste.com.br");

        customer = customers.ofId(customer.id()).orElseThrow();
        customer.changeName(newName);
        customer.changeEmail(newEmail);

        customers.add(customer);

        customer = customers.ofId(customer.id()).orElseThrow();
        assertThat(customer.fullName()).isEqualTo(newName);
        assertThat(customer.email()).isEqualTo(newEmail);
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var customerT1 = customers.ofId(customer.id()).orElseThrow();
        var customerT2 = customers.ofId(customer.id()).orElseThrow();

        customerT1.archive();
        customers.add(customerT1);

        customerT2.changeName(FullName.of("testando", "testmhp"));

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(()-> customers.add(customerT2));

        var savedCustomer = customers.ofId(customer.id()).orElseThrow();
        assertThat(savedCustomer.archivedAt()).isNotNull();
        assertThat(savedCustomer.isArchived()).isTrue();
    }

}
