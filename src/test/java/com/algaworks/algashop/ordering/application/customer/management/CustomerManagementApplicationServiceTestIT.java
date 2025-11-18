package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import com.algaworks.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static com.algaworks.algashop.ordering.application.customer.management.CustomerInputTestFixture.aCustomer;
import static com.algaworks.algashop.ordering.application.customer.management.CustomerInputTestFixture.aCustomerFilipe;
import static com.algaworks.algashop.ordering.application.customer.management.CustomerUpdateInputTestFixture.aCustomerUpdate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomerManagementApplicationServiceTestIT {

    private final CustomerManagementApplicationService customerManagementApplicationService;
    private final CustomerQueryService queryService;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoSpyBean
    private CustomerNotificationApplicationService customerNotificationApplicationService;

    @Test
    void shouldRegister() {
        var input = aCustomer().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        var customerOutput = queryService.findById(customerId);
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

        Mockito.verify(customerEventListener).listen(Mockito.any(CustomerRegisteredEvent.class));
        Mockito.verify(customerEventListener, Mockito.never()).listen(Mockito.any(CustomerArchivedEvent.class));
        Mockito.verify(customerNotificationApplicationService).notifyNewRegistration(
                Mockito.any(NotifyNewRegistrationInput.class));
    }

    @Test
    void shouldUpdate() {
        var input = aCustomer().build();
        var updateInput = aCustomerUpdate().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        customerManagementApplicationService.update(customerId, updateInput);

        var customerOutput = queryService.findById(customerId);

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

        var customerOutput = queryService.findById(customerId);
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

        var customerOutput = queryService.findById(customerId);

        customerManagementApplicationService.changeEmail(customerOutput.getId(), newEmail);

        var customerChangedEmail = queryService.findById(customerOutput.getId());
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

        var customerToChangeEmail = queryService.findById(customerId);
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerToChangeEmail.getId(), "teste@gmail.com"));
    }

    @Test
    void shouldThrowExceptionEmailInvalid_when_ChangeEmail() {
        var input = aCustomer().build();

        var customerId = customerManagementApplicationService.create(input);
        assertThat(customerId).isNotNull();

        var customerToChangeEmail = queryService.findById(customerId);
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

        var customerT2ToChangeEmail = queryService.findById(customerIdT2);

        Assertions.assertThatExceptionOfType(CustomerEmailIsInUseException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerT2ToChangeEmail.getId(), "johndoe@email.com"));
    }

}