package com.algaworks.algashop.ordering.infrastructure.notification;

import com.algaworks.algashop.ordering.application.order.notification.OrderNotificationApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderNotificationApplicationServiceFakeImpl implements OrderNotificationApplicationService {

    @Override
    public void notifyNewRegistration(NotifyNewRegistrationInput input) {
        log.info("Order Notification {}", input.orderId());
        log.info("Customer Notification {}", input.customerId());
        log.info("Registered {}", input.registeredAt());
    }
}
