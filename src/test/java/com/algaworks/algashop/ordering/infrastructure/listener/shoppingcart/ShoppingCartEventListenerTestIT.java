package com.algaworks.algashop.ordering.infrastructure.listener.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;

@SpringBootTest
class ShoppingCartEventListenerTestIT {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @MockitoSpyBean
    private ShoppingCartEventListener shoppingCartEventListener;

    @Test
    public void shouldListenShoppingCreatedEvent() {
        applicationEventPublisher.publishEvent(
                ShoppingCartCreatedEvent.of(
                        ShoppingCartId.of(),
                        CustomerId.of(),
                        OffsetDateTime.now()));

        Mockito.verify(shoppingCartEventListener).listen(Mockito.any(ShoppingCartCreatedEvent.class));
    }

    @Test
    public void shouldListenShoppingCartEmptiedEvent() {
        applicationEventPublisher.publishEvent(
                ShoppingCartEmptiedEvent.of(
                        ShoppingCartId.of(),
                        CustomerId.of(),
                        OffsetDateTime.now()));

        Mockito.verify(shoppingCartEventListener).listen(Mockito.any(ShoppingCartEmptiedEvent.class));
    }

    @Test
    public void shouldListenShoppingCartItemRemovedEvent() {
        applicationEventPublisher.publishEvent(
                ShoppingCartItemRemovedEvent.of(
                        ShoppingCartId.of(),
                        CustomerId.of(),
                        ProductId.of(),
                        OffsetDateTime.now()));

        Mockito.verify(shoppingCartEventListener).listen(Mockito.any(ShoppingCartItemRemovedEvent.class));
    }

    @Test
    public void shouldListenShoppingCartItemAddedEvent() {
        applicationEventPublisher.publishEvent(
                ShoppingCartItemAddedEvent.of(
                        ShoppingCartId.of(),
                        CustomerId.of(),
                        ProductId.of(),
                        OffsetDateTime.now()));

        Mockito.verify(shoppingCartEventListener).listen(Mockito.any(ShoppingCartItemAddedEvent.class));
    }

}