package com.algaworks.algashop.ordering.application.customer.notification;

import java.util.UUID;

public interface CustomerNotificationApplicationService {

    void notifyNewRegistration(NotifyNewRegistrationInput input);

    record NotifyNewRegistrationInput(UUID customerId, String firstName, String email) {

        public static NotifyNewRegistrationInput of(UUID customerId,String firstName, String email) {
            return new NotifyNewRegistrationInput(customerId, firstName, email);
        }
    }

}
