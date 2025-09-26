package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.commons.mappers.CustomerMapper;
import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {

    private final CustomerMapper customerMapper;
    private final CustomerRegistrationService customerRegistrationService;
    private final Customers customers;

    @Transactional
    public UUID create(CustomerInput input) {
        Objects.requireNonNull(input);
        var address = input.getAddress();

        var customer = registerCustomer(input, address);
        customers.add(customer);

        return customer.id().value();
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public CustomerOutput findById(UUID customerId) {
        Objects.requireNonNull(customerId);

        var customer = customers.ofId(CustomerId.from(customerId))
                .orElseThrow(() -> CustomerNotFoundException.because(customerId));

        return customerMapper.toOutput(customer);
    }

    @Transactional
    public void archive(UUID customerId) {
        Objects.requireNonNull(customerId);

        var customer = customers.ofId(CustomerId.from(customerId))
                .orElseThrow(() -> CustomerNotFoundException.because(customerId));

        customer.archive();
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
                getAddress(address)
        );
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
