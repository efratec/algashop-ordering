package com.algaworks.algashop.ordering.core.ports.out.customer;

import java.util.UUID;

public interface ForNotifyingCustomers {

    void notifyNewRegistration(NotifyNewRegistrationInput input);

    record NotifyNewRegistrationInput(UUID customerId, String firstName, String email) {

        public static NotifyNewRegistrationInput of(UUID customerId,String firstName, String email) {
            return new NotifyNewRegistrationInput(customerId, firstName, email);
        }
    }

}
