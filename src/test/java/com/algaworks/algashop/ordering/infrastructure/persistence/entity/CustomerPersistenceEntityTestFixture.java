package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity.CustomerPersistenceEntityBuilder;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityAssembler.convertAddressToEmbeddable;

public class CustomerPersistenceEntityTestFixture {

    public static CustomerPersistenceEntityBuilder aCustomer() {
        return CustomerPersistenceEntity.builder();
    }

    public static CustomerPersistenceEntityBuilder existingCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(DEFAULT_CUSTOMER_ID.value())
                .firstName("John")
                .lastName("Smith")
                .birthDate(LocalDate.now())
                .email("teste@gmail.com")
                .phone("123456789")
                .document("Document")
                .promotionNotificationsAllowed(true)
                .archived(false)
                .registeredAt(OffsetDateTime.now())
                .archivedAt(null)
                .loyaltyPoints(2)
                .address(convertAddressToEmbeddable(Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("York")
                        .state("South California")
                        .zipCode(ZipCode.of("12345"))
                        .complement("Apt. 114")
                        .build()));
    }

}
