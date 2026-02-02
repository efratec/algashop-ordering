package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.brandNewCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@Import({CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

    @Test
    void shouldFindByEmail() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var customerOptional = customers.ofEmail(customer.email());
        assertThat(customerOptional).isPresent();
    }

    @Test
    void shouldNotFindByEmailIfNoCustomerExistsWithEmail() {
        var customerOptional = customers.ofEmail(Email.of(UUID.randomUUID() + "@gmail.com"));
        assertThat(customerOptional).isNotPresent();
    }

    @Test
    void shouldReturnIfEmailIsInUse() {
                var customer = CustomerTestFixture.brandNewCustomer().build();
        customers.add(customer);

        assertThat(customers.isEmailUnique(customer.email(), customer.id())).isTrue();
        assertThat(customers.isEmailUnique(customer.email(), CustomerId.of())).isFalse();
        assertThat(customers.isEmailUnique(Email.of("teste@gmail.com"), CustomerId.of())).isTrue();
    }

}
