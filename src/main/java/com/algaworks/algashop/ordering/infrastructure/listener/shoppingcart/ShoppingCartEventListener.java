package com.algaworks.algashop.ordering.infrastructure.listener.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCreatedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartEmptiedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemAddedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemRemovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShoppingCartEventListener {

    @EventListener
    public void listen(ShoppingCartCreatedEvent event) {
        log.info("Shopping cart created: {}", event);
    }

    @EventListener
    public void listen(ShoppingCartEmptiedEvent event) {
        log.info("Shopping cart emptied: {}", event);

    }

    @EventListener
    public void listen(ShoppingCartItemAddedEvent event) {
        log.info("Shopping cart item added: {}", event);
    }

    @EventListener
    public void listen(ShoppingCartItemRemovedEvent event) {
        log.info("Shopping cart item removed: {}", event);
    }

}
