package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler.convertAddressToEmbeddable;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CustomerPersistenceEntityAssemblerTest {

    @InjectMocks
    private CustomerPersistenceEntityAssembler assembler;

    @Test
    void shouldConvertToDomainCustomer() {
        var customer = CustomerTestFixture.existingCustomer().build();
        var customerPersistenceEntity = assembler.fromDomain(customer);
        assertThat(customerPersistenceEntity).satisfies(
                c -> assertThat(c.getId()).isEqualTo(customer.id().value()),
                c -> assertThat(c.getFirstName()).isEqualTo(customer.fullName().firstName()),
                c -> assertThat(c.getLastName()).isEqualTo(customer.fullName().lastName()),
                c -> assertThat(c.getEmail()).isEqualTo(customer.email().value()),
                c -> assertThat(c.getPhone()).isEqualTo(customer.phone().value()),
                c -> assertThat(c.getDocument()).isEqualTo(customer.document().value()),
                c -> assertThat(c.getPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed()),
                c -> assertThat(c.getArchived()).isEqualTo(customer.isArchived()),
                c -> assertThat(c.getAddress()).isEqualTo(convertAddressToEmbeddable(customer.address()))
        );
    }

}
