package com.algaworks.algashop.ordering.domain.model.entity.fixture;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;


public class CustomerTestFixture {

    public static final CustomerId DEFAULT_CUSTOMER_ID = new CustomerId(UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a"));

    public static Customer.BrandNewCustomerBuild brandNewCustomer() {
        return Customer.brandNew()
                .fullName(FullName.of("John","Doe"))
                .birthDate(BirthDate.of(LocalDate.of(1991, 7,5)))
                .email(Email.of("johndoe@email.com"))
                .phone(Phone.of("478-256-2604"))
                .document(Document.of("255-08-0578"))
                .promotionNotificationsAllowed(true)
                .address(Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("York")
                        .state("South California")
                        .zipCode(new ZipCode("12345"))
                        .complement("Apt. 114")
                        .build());
    }

    public static Customer.ExistingCustomerBuild existingCustomer() {
        return Customer.existing()
                .id(DEFAULT_CUSTOMER_ID)
                .registeredAt(OffsetDateTime.now())
                .promotionNotificationsAllowed(true)
                .archived(false)
                .archivedAt(OffsetDateTime.now())
                .fullName(FullName.of("John","Doe"))
                .birthDate(BirthDate.of(LocalDate.of(1991, 7,5)))
                .email(Email.of("john" + UUID.randomUUID() + "@gmail.com"))
                .phone(Phone.of("478-256-2604"))
                .document(Document.of("255-08-0578"))
                .promotionNotificationsAllowed(true)
                .loyaltyPoints(LoyaltyPoints.ZERO)
                .address(Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("York")
                        .state("South California")
                        .zipCode(ZipCode.of("12345"))
                        .complement("Apt. 114")
                        .build());
    }

    public static Customer.ExistingCustomerBuild existingAnonymizedCustomer() {
        return Customer.existing()
                .id(CustomerId.of())
                .fullName(FullName.of("Anonymous", "Anonymous"))
                .birthDate(null)
                .email(Email.of("anonymous@anonymous.com"))
                .phone(Phone.of("000-000-0000"))
                .document(Document.of("000-00-0000"))
                .promotionNotificationsAllowed(false)
                .archived(true)
                .registeredAt(OffsetDateTime.now())
                .archivedAt(OffsetDateTime.now())
                .loyaltyPoints(LoyaltyPoints.of(10))
                .address(Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("York")
                        .state("South California")
                        .zipCode(ZipCode.of("12345"))
                        .complement("Apt. 114")
                        .build());
    }

}
