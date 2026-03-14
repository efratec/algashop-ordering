package com.algaworks.algashop.ordering.infrastructure.adapters.in.listener.order;

import com.algaworks.algashop.ordering.core.domain.model.order.OrderCanceledEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderPaidEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderPlacedEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    @EventListener
    public void listen(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent {}", event);
    }

    @EventListener
    public void listen(OrderPaidEvent event) {
        log.info("Received OrderPaidEvent {}", event);
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        log.info("Received OrderReadyEvent {}", event);
    }

    @EventListener
    public void listen(OrderCanceledEvent event) {
        log.info("Received OrderCanceledEvent {}", event);
    }

}
