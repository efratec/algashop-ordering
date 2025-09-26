package com.algaworks.algashop.ordering.application.service.customer.management;

import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.application.customer.management.CustomerOutput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
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
import static com.algaworks.algashop.ordering.application.service.customer.management.CustomerInputTestFixture.aCustomerFilipe;
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
    void shouldChangeEmail() {
        var newEmail = "novoemail@gmail.com";
        var input = aCustomer().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        var customerOutput = customerManagementApplicationService.findById(customerId);

        customerManagementApplicationService.changeEmail(customerOutput.getId(), newEmail);

        var customerChangedEmail = customerManagementApplicationService.findById(customerOutput.getId());
        assertThat(customerChangedEmail).isNotNull();
        assertThat(customerChangedEmail.getEmail()).isEqualTo(newEmail);
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

    @Test
    void shouldThrowExceptionCustomerNotFound_when_change_email() {
        var customerIdNotExisting = UUID.randomUUID();
        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerIdNotExisting, "teste@gmail.com"));
    }

    @Test
    void shouldThrowExceptionChangeEmail_when_Customer_Not_Found() {
        var customerIdNotExisting = UUID.randomUUID();
        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerIdNotExisting, "teste@gmail.com"));
    }

    @Test
    void shouldThrowExceptionCustomerAlreadyArchived_when_ChangeEmail() {
        var input = aCustomer().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        var customerToChangeEmail = customerManagementApplicationService.findById(customerId);
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerToChangeEmail.getId(), "teste@gmail.com"));
    }

    @Test
    void shouldThrowExceptionEmailInvalid_when_ChangeEmail() {
        var input = aCustomer().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        var customerToChangeEmail = customerManagementApplicationService.findById(customerId);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerToChangeEmail.getId(), "email-invalid"));
    }

    @Test
    void shouldThrowException_whenChangingEmailToExistingOne() {
        var inputT1 = aCustomer().build();
        var inputT2 = aCustomerFilipe().build();

        var customerIdT1 = customerManagementApplicationService.create(inputT1);
        assertThat(customerIdT1).isNotNull();

        var customerIdT2 = customerManagementApplicationService.create(inputT2);
        assertThat(customerIdT2).isNotNull();

        var customerT2ToChangeEmail = customerManagementApplicationService.findById(customerIdT2);

        Assertions.assertThatExceptionOfType(CustomerEmailIsInUseException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerT2ToChangeEmail.getId(), "johndoe@email.com"));
    }

}