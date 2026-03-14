package com.algaworks.algashop.ordering.core.application.customer;

import com.algaworks.algashop.ordering.core.ports.in.commons.AddressData;
import com.algaworks.algashop.ordering.core.ports.in.customer.CustomerInput;
import com.algaworks.algashop.ordering.core.ports.in.customer.CustomerUpdateInput;
import com.algaworks.algashop.ordering.core.ports.in.customer.ForManagingCustomers;
import com.algaworks.algashop.ordering.core.domain.model.commons.*;
import com.algaworks.algashop.ordering.core.domain.model.customer.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService implements ForManagingCustomers {

    private final CustomerRegistrationService customerRegistrationService;
    private final Customers customers;

    @Transactional
    @Override
    public UUID create(CustomerInput input) {
        Objects.requireNonNull(input);
        var address = input.getAddress();

        var customer = registerCustomer(input, address);
        customers.add(customer);

        return customer.id().value();
    }

    @Transactional
    @Override
    public void update(UUID rawCustomerId, CustomerUpdateInput input) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(rawCustomerId);

        var customer = customers.ofId(CustomerId.from(rawCustomerId))
                .orElseThrow(() -> CustomerNotFoundException.because(rawCustomerId));

        customer.changeName(FullName.of(input.getFirstName(), input.getLastName()));
        customer.changePhone(Phone.of(input.getPhone()));

        if (Boolean.TRUE.equals(input.getPromotionNotificationsAllowed())) {
            customer.enablePromotionNotifications();
        } else {
            customer.disablePromotionNotifications();
        }

        var address = input.getAddress();
        customer.changeAddress(getAddress(address));

        customers.add(customer);
    }

    @Transactional
    @Override
    public void archive(UUID customerId) {
        Objects.requireNonNull(customerId);

        var customer = customers.ofId(CustomerId.from(customerId))
                .orElseThrow(() -> CustomerNotFoundException.because(customerId));

        customer.archive();
        customers.add(customer);
    }

    @Transactional
    @Override
    public void changeEmail(UUID customerId, String newEmail) {
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(newEmail);

        var customer = customers.ofId(CustomerId.from(customerId))
                .orElseThrow(() -> CustomerNotFoundException.because(customerId));

        customerRegistrationService.changeEmail(customer, Email.of(newEmail));

        customers.add(customer);
    }

   private Customer registerCustomer(CustomerInput input, AddressData address) {
       return customerRegistrationService.register(
               FullName.of(input.getFirstName(), input.getLastName()),
               BirthDate.of(input.getBirthDate()),
               Email.of(input.getEmail()),
               Phone.of(input.getPhone()),
               Document.of(input.getDocument()),
               input.getPromotionNotificationsAllowed(),
               getAddress(address));
   }

    private static Address getAddress(AddressData address) {
        return Address.builder()
                .zipCode(ZipCode.of(address.getZipCode()))
                .state(address.getState())
                .number(address.getNumber())
                .city(address.getCity())
                .complement(address.getComplement())
                .street(address.getStreet())
                .neighborhood(address.getNeighborhood())
                .number(address.getNumber())
                .build();
    }

}
