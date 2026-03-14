package com.algaworks.algashop.ordering.core.domain.model.customer;

import com.algaworks.algashop.ordering.core.domain.model.commons.*;
import com.algaworks.algashop.ordering.core.domain.AbstractDomainIT;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomerRegistrationServiceIT extends AbstractDomainIT {

    private final CustomerRegistrationService service;

    @Test
    void shouldRegisterCustomer() {
        var customer = service.register(
                FullName.of("John", "Doe"),
                BirthDate.of(LocalDate.of(1991, 7, 5)),
                Email.of("johndoe@email.com"),
                Phone.of("478-256-2604"),
                Document.of("255-08-0578"),
                true,
                Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("Yostfort")
                        .state("South Carolina")
                        .zipCode(ZipCode.of("70283"))
                        .complement("Apt. 901")
                        .build());
        Assertions.assertThat(customer.fullName()).isEqualTo(FullName.of("John", "Doe"));
        Assertions.assertThat(customer.email()).isEqualTo(Email.of("johndoe@email.com"));
    }

}