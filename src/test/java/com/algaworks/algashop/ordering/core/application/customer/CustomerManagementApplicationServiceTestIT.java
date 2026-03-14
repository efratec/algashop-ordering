package com.algaworks.algashop.ordering.core.application.customer;

import com.algaworks.algashop.ordering.core.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.core.ports.in.customer.ForManagingCustomers;
import com.algaworks.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import com.algaworks.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers.NotifyNewRegistrationInput;
import com.algaworks.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.algaworks.algashop.ordering.core.ports.in.customer.ForQueryingCustomers;
import com.algaworks.algashop.ordering.core.domain.model.customer.*;
import com.algaworks.algashop.ordering.infrastructure.adapters.in.listener.customer.CustomerEventListener;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;
import java.util.UUID;

import static com.algaworks.algashop.ordering.core.application.customer.CustomerInputTestFixture.aCustomer;
import static com.algaworks.algashop.ordering.core.application.customer.CustomerInputTestFixture.aCustomerFilipe;
import static com.algaworks.algashop.ordering.core.application.customer.CustomerUpdateInputTestFixture.aCustomerUpdate;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomerManagementApplicationServiceTestIT extends AbstractApplicationIT {

    private final ForManagingCustomers forManagingCustomers;
    private final ForQueryingCustomers queryService;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoSpyBean
    private ForNotifyingCustomers forNotifyingCustomers;

    @Test
    void shouldRegister() {
        var input = aCustomer().build();

        var customerId = forManagingCustomers.create(input);
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
                        LocalDate.of(1991, 7, 5)
                );
        assertThat(customerOutput.getRegisteredAt()).isNotNull();

        Mockito.verify(customerEventListener).listen(Mockito.any(CustomerRegisteredEvent.class));
        Mockito.verify(customerEventListener, Mockito.never()).listen(Mockito.any(CustomerArchivedEvent.class));
        Mockito.verify(forNotifyingCustomers).notifyNewRegistration(
                Mockito.any(NotifyNewRegistrationInput.class));
    }

    @Test
    void shouldUpdate() {
        var input = aCustomer().build();
        var updateInput = aCustomerUpdate().build();

        var customerId = forManagingCustomers.create(input);
        assertThat(customerId).isNotNull();

        forManagingCustomers.update(customerId, updateInput);

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
                        LocalDate.of(1991, 7, 5)
                );
        assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }

    @Test
    void shouldArchive() {
        var input = aCustomer().build();

        var customerId = forManagingCustomers.create(input);
        assertThat(customerId).isNotNull();

        forManagingCustomers.archive(customerId);

        var customerOutput = queryService.findById(customerId);
        assertThat(customerOutput).isNotNull();
        assertThat(customerOutput.getArchivedAt()).isNotNull();
        assertThat(customerOutput.getArchived()).isTrue();
    }

    @Test
    void shouldChangeEmail() {
        var newEmail = "novoemail@gmail.com";
        var input = aCustomer().build();

        var customerId = forManagingCustomers.create(input);
        assertThat(customerId).isNotNull();

        var customerOutput = queryService.findById(customerId);

        forManagingCustomers.changeEmail(customerOutput.getId(), newEmail);

        var customerChangedEmail = queryService.findById(customerOutput.getId());
        assertThat(customerChangedEmail).isNotNull();
        assertThat(customerChangedEmail.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void shouldThrowException_whenArchivingNonexistentCustomer() {
        var customerIdNotExisting = UUID.randomUUID();
        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> forManagingCustomers.archive(customerIdNotExisting));
    }

    @Test
    void shouldThrowExceptionCustomerArchived_when_ArchivingCustomerIsArchived() {
        var input = aCustomer().build();

        var customerId = forManagingCustomers.create(input);
        assertThat(customerId).isNotNull();

        forManagingCustomers.archive(customerId);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> forManagingCustomers.archive(customerId));
    }

    @Test
    void shouldThrowExceptionCustomerNotFound_when_change_email() {
        var customerIdNotExisting = UUID.randomUUID();
        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> forManagingCustomers.changeEmail(customerIdNotExisting, "teste@gmail.com"));
    }

    @Test
    void shouldThrowExceptionChangeEmail_when_Customer_Not_Found() {
        var customerIdNotExisting = UUID.randomUUID();
        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> forManagingCustomers.changeEmail(customerIdNotExisting, "teste@gmail.com"));
    }

    @Test
    void shouldThrowExceptionCustomerAlreadyArchived_when_ChangeEmail() {
        var input = aCustomer().build();

        var customerId = forManagingCustomers.create(input);
        assertThat(customerId).isNotNull();

        forManagingCustomers.archive(customerId);

        var customerToChangeEmail = queryService.findById(customerId);
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> forManagingCustomers.changeEmail(customerToChangeEmail.getId(), "teste@gmail.com"));
    }

    @Test
    void shouldThrowExceptionEmailInvalid_when_ChangeEmail() {
        var input = aCustomer().build();

        var customerId = forManagingCustomers.create(input);
        assertThat(customerId).isNotNull();

        var customerToChangeEmail = queryService.findById(customerId);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> forManagingCustomers.changeEmail(customerToChangeEmail.getId(), "email-invalid"));
    }

    @Test
    void shouldThrowException_whenChangingEmailToExistingOne() {
        var inputT1 = aCustomer().build();
        var inputT2 = aCustomerFilipe().build();

        var customerIdT1 = forManagingCustomers.create(inputT1);
        assertThat(customerIdT1).isNotNull();

        var customerIdT2 = forManagingCustomers.create(inputT2);
        assertThat(customerIdT2).isNotNull();

        var customerT2ToChangeEmail = queryService.findById(customerIdT2);

        Assertions.assertThatExceptionOfType(CustomerEmailIsInUseException.class)
                .isThrownBy(() -> forManagingCustomers.changeEmail(customerT2ToChangeEmail.getId(), "johndoe@email.com"));
    }

}