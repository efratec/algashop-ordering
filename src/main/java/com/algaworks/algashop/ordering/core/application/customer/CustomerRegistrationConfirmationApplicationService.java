package com.algaworks.algashop.ordering.core.application.customer;

import com.algaworks.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import com.algaworks.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import com.algaworks.algashop.ordering.core.ports.out.customer.ForObtainingCustomers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationConfirmationApplicationService implements ForConfirmCustomerRegistration {

    private final ForNotifyingCustomers forNotifyingCustomers;
    private final ForObtainingCustomers forObtainingCustomers;

    @Override
    public void confirm(UUID customerId) {
        var customersOutput = forObtainingCustomers.findById(customerId);
        var input = ForNotifyingCustomers.NotifyNewRegistrationInput.of(
                customersOutput.getId(),
                customersOutput.getFirstName(),
                customersOutput.getEmail());
        forNotifyingCustomers.notifyNewRegistration(input);
    }

}
