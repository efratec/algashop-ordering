package com.algaworks.algashop.ordering.application.shoppingcart.management;

import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.*;
import com.algaworks.algashop.ordering.infrastructure.listener.shoppingcart.ShoppingCartEventListener;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.brandNewCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShoppingCartManagementApplicationServiceTestIT {

    private final ShoppingCarts shoppingCarts;
    private final Customers customers;
    private final ShoppingCartManagementApplicationService applicationService;

    @MockitoSpyBean
    private ShoppingCartEventListener shoppingCartEventListener;

    @MockitoBean
    private final ProductCatalogService productCatalogService;

    @Test
    void shouldAddItemToCart_whenProductIsInStock() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        var product = aProduct().inStock(true).build();
        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));

        var input = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(product.id().value())
                .quantity(3)
                .build();

        applicationService.addItem(input);

        var updatedCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        assertThat(updatedCart.items()).hasSize(1);
        assertThat(updatedCart.items().iterator().next().productId()).isEqualTo(product.id());
        assertThat(updatedCart.items().iterator().next().quantity().value()).isEqualTo(3);

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartCreatedEvent.class));

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartItemAddedEvent.class));
    }

    @Test
    void shouldThrowException_whenAddingItemToNonexistentCart() {
        var cartIdDoesNotExist = UUID.randomUUID();
        var product = aProduct().inStock(true).build();

        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));

        var input = ShoppingCartItemInput.builder()
                .shoppingCartId(cartIdDoesNotExist)
                .productId(product.id().value())
                .quantity(1)
                .build();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> applicationService.addItem(input));
    }

    @Test
    void shouldThrowException_whenAddingNonexistentProductToCart() {
        when(productCatalogService.ofId(Mockito.any())).thenReturn(Optional.empty());

        var customer = brandNewCustomer().build();
        customers.add(customer);

        var shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        ShoppingCartItemInput input = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(UUID.randomUUID())
                .quantity(1)
                .build();

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> applicationService.addItem(input));
    }

    @Test
    void shouldThrowException_whenAddingOutOfStockProductToCart() {
        var customer = brandNewCustomer().build();
        customers.add(customer);
        assertThat(customer.id()).isNotNull();

        var shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        var productOutStock = aProduct().inStock(false).build();
        when(productCatalogService.ofId(productOutStock.id())).thenReturn(Optional.of(productOutStock));

        var input = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(productOutStock.id().value())
                .quantity(3)
                .build();

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> applicationService.addItem(input));
    }

    @Test
    void shouldCreateCart_whenCustomerHasNoCart() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var shoppingCartId = applicationService.createNew(customer.id().value());
        assertThat(shoppingCartId).isNotNull();

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartCreatedEvent.class));
    }

    @Test
    void shouldRemoveItemFromCart_whenItemExists() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var productInStock = aProduct().inStock(true).build();
        when(productCatalogService.ofId(productInStock.id()))
                .thenReturn(Optional.of(productInStock));

        var cartId = applicationService.createNew(customer.id().value());

        final int quantity = 3;
        var input = ShoppingCartItemInput.builder()
                .shoppingCartId(cartId)
                .productId(productInStock.id().value())
                .quantity(quantity)
                .build();

        applicationService.addItem(input);

        var cart = shoppingCarts.ofId(ShoppingCartId.from(cartId)).orElseThrow();
        assertThat(cart.items()).hasSize(1);

        var itemIdToRemove = cart.items().stream().findFirst().orElseThrow().id().value();
        applicationService.removeItem(cart.id().value(), itemIdToRemove);

        cart = shoppingCarts.ofId(ShoppingCartId.from(cartId)).orElseThrow();
        assertThat(cart.items()).isEmpty();

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartCreatedEvent.class));

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartItemAddedEvent.class));

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartItemRemovedEvent.class));

    }

    @Test
    void shouldThrowException_whenRemovingItemFromNonexistentCart() {
        UUID cartIdDoesNotExist = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> applicationService.removeItem(cartIdDoesNotExist, itemId));
    }


    @Test
    void shouldThrowException_whenRemovingNonexistentItemFromCart() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        UUID itemIdDoesNotExist = UUID.randomUUID();

        assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() -> applicationService.removeItem(shoppingCart.id().value(), itemIdDoesNotExist));
    }

    @Test
    void shouldEmptyCart_whenCartHasItems() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var shoppingCartId = applicationService.createNew(customer.id().value());

        var productInStock = aProduct().inStock(true).build();
        when(productCatalogService.ofId(productInStock.id()))
                .thenReturn(Optional.of(productInStock));

        final int quantity = 3;
        var input = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCartId)
                .productId(productInStock.id().value())
                .quantity(quantity)
                .build();

        applicationService.addItem(input);

        var shoppingCart = shoppingCarts.ofId(ShoppingCartId.from(shoppingCartId)).orElseThrow();

        applicationService.empty(shoppingCart.id().value());

        shoppingCart = shoppingCarts.ofId(ShoppingCartId.from(shoppingCartId)).orElseThrow();
        assertThat(shoppingCart.isEmpty()).isTrue();

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartCreatedEvent.class));

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartItemAddedEvent.class));

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartEmptiedEvent.class));

    }

    @Test
    void shouldThrowException_whenEmptyingNonexistentCart() {
        UUID cartIdDoesNotExist = UUID.randomUUID();
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> applicationService.empty(cartIdDoesNotExist));
    }

    @Test
    void shouldDeleteCart_whenCartExists() {
        var customer = brandNewCustomer().build();
        customers.add(customer);

        var shoppingCartId = applicationService.createNew(customer.id().value());

        applicationService.delete(shoppingCartId);

        var cartDeleted = shoppingCarts.ofId(ShoppingCartId.from(shoppingCartId));
        assertThat(cartDeleted).isNotPresent();

        Mockito.verify(shoppingCartEventListener, Mockito.times(1))
                .listen(Mockito.any(ShoppingCartCreatedEvent.class));
    }

    @Test
    void shouldThrowException_whenDeletingNonexistentCart() {
        UUID cartIdDoesNotExist = UUID.randomUUID();
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> applicationService.delete(cartIdDoesNotExist));
    }

}