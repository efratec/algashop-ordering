package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.application.order.notification.OrderNotificationApplicationService;
import com.algaworks.algashop.ordering.application.order.notification.OrderNotificationApplicationService.NotifyNewRegistrationInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderNotificationApplicationService orderNotificationApplicationService;

    @EventListener
    public void listen(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent {}", event);
        orderNotificationApplicationService.notifyNewRegistration(
                NotifyNewRegistrationInput.of(event.orderId(),
                        event.customerId(), event.placedAt()));
    }

    @EventListener
    public void listen(OrderPaidEvent event) {
        log.info("Received OrderPaidEvent {}", event);
        orderNotificationApplicationService.notifyNewRegistration(
                NotifyNewRegistrationInput.of(event.orderId(),
                        event.customerId(), event.paidAt()));
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        log.info("Received OrderReadyEvent {}", event);
        orderNotificationApplicationService.notifyNewRegistration(
                NotifyNewRegistrationInput.of(event.orderId(),
                        event.customerId(), event.readyAt()));
    }

    @EventListener
    public void listen(OrderCanceledEvent event) {
        log.info("Received OrderCanceledEvent {}", event);
        orderNotificationApplicationService.notifyNewRegistration(
                NotifyNewRegistrationInput.of(event.orderId(),
                        event.customerId(), event.canceledAt()));
    }

}
