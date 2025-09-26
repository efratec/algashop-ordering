package com.algaworks.algashop.ordering.application.service.customer.management;

import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.application.customer.management.CustomerOutput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static com.algaworks.algashop.ordering.application.service.customer.management.CustomerInputTestFixture.aCustomer;
import static com.algaworks.algashop.ordering.application.service.customer.management.CustomerUpdateInputTestFixture.aCustomerUpdate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomerManagementApplicationServiceIT {

    private final CustomerManagementApplicationService customerManagementApplicationService;

    @Test
    void shouldRegister() {
        var input = aCustomer().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        var customerOutput = customerManagementApplicationService.findById(customerId);
        assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getEmail,
                        CustomerOutput::getBirthDate
                ).containsExactly(
                        customerId,
                        "John",
                        "Doe",
                        "johndoe@email.com",
                        LocalDate.of(1991, 7,5)
                );
        assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }

    @Test
    void shouldUpdate() {
        var input = aCustomer().build();
        var updateInput = aCustomerUpdate().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        customerManagementApplicationService.update(customerId, updateInput);

        var customerOutput = customerManagementApplicationService.findById(customerId);

        assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getEmail,
                        CustomerOutput::getBirthDate
                ).containsExactly(
                        customerId,
                        "Matt",
                        "Damon",
                        "johndoe@email.com",
                        LocalDate.of(1991, 7,5)
                );
        assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }

    @Test
    void shouldArchive() {
        var input = aCustomer().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        var customerOutput = customerManagementApplicationService.findById(customerId);
        assertThat(customerOutput).isNotNull();
        assertThat(customerOutput.getArchivedAt()).isNotNull();
        assertThat(customerOutput.getArchived()).isTrue();
    }

    @Test
    void shouldThrowException_whenArchivingNonexistentCustomer() {
        var customerIdNotExisting = UUID.randomUUID();
        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> customerManagementApplicationService.archive(customerIdNotExisting));
    }

    @Test
    void shouldThrowExceptionCustomerArchived_when_ArchivingCustomerIsArchived() {
        var input = aCustomer().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customerManagementApplicationService.archive(customerId));
    }

}