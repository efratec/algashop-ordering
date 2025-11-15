package com.algaworks.algashop.ordering.application.order.notification;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;

import java.time.OffsetDateTime;

public interface OrderNotificationApplicationService {

    void notifyNewRegistration(NotifyNewRegistrationInput input);

    record NotifyNewRegistrationInput(OrderId orderId, CustomerId customerId, OffsetDateTime registeredAt) {

        public static OrderNotificationApplicationService.NotifyNewRegistrationInput of(OrderId orderId,
                                                                                        CustomerId customerId,
                                                                                        OffsetDateTime registeredAt) {
            return new OrderNotificationApplicationService.NotifyNewRegistrationInput(orderId, customerId, registeredAt);
        }
    }

}
