package com.algaworks.algashop.ordering.domain.model.entity.fixture;

import com.algaworks.algashop.ordering.domain.model.utility.GeneratorId;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity.CustomerPersistenceEntityBuilder;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler.convertAddressToEmbeddable;

public class CustomerPersistenceEntityTestFixture {

    public static CustomerPersistenceEntityBuilder existingCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(GeneratorId.generateTimeBasedUUID())
                .firstName("John")
                .lastName("Smith")
                .birthDate(LocalDate.now())
                .email("teste@gmail.com")
                .phone("123456789")
                .document("Document")
                .promotionNotificationsAllowed(true)
                .archived(true)
                .registeredAt(OffsetDateTime.now())
                .archivedAt(OffsetDateTime.now())
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
