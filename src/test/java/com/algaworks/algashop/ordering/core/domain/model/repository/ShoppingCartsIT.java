package com.algaworks.algashop.ordering.core.domain.model.repository;

import com.algaworks.algashop.ordering.core.domain.AbstractDomainIT;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.algaworks.algashop.ordering.core.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.core.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static com.algaworks.algashop.ordering.core.domain.model.entity.fixture.ShoppingCartTestFixture.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;

@Import({ShoppingCartPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShoppingCartsIT extends AbstractDomainIT {

    private final ShoppingCarts shoppingCarts;
    private final Customers customers;

    @BeforeEach
    void setup() {
        if (!customers.exists(DEFAULT_CUSTOMER_ID)) {
            customers.add(existingCustomer().build());
        }
    }

    @Test
    void shouldPersistAndFind() {
        var originalShoppingCart = aShoppingCart().withItems(true).build();
        shoppingCarts.add(originalShoppingCart);

        var possibleShoppingCart = shoppingCarts.ofId(originalShoppingCart.id());
        assertThat(possibleShoppingCart).isPresent();

        var savedShoppingCart = possibleShoppingCart.get();

        assertThat(savedShoppingCart).satisfies(shoppingCart -> {
           assertThat(shoppingCart.id()).isEqualTo(savedShoppingCart.id());
           assertThat(shoppingCart.customerId()).isEqualTo(savedShoppingCart.customerId());
           assertThat(shoppingCart.totalItems().value()).isEqualTo(savedShoppingCart.totalItems().value());
           assertThat(shoppingCart.items()).hasSameSizeAs(savedShoppingCart.items());
           assertThat(shoppingCart.id()).isEqualTo(savedShoppingCart.id());
        });
    }

    @Test
    void shouldFindShoppingCartByCustomer() {
        var originalShoppingCart = aShoppingCart().withItems(true).build();
        shoppingCarts.add(originalShoppingCart);

        var possibleShoppingCart = shoppingCarts.ofCustomer(originalShoppingCart.customerId());
        assertThat(possibleShoppingCart).isPresent();
        var savedShoppingCart = possibleShoppingCart.get();

        assertThat(savedShoppingCart).satisfies(shoppingCart -> {
            assertThat(shoppingCart.id()).isEqualTo(savedShoppingCart.id());
            assertThat(shoppingCart.customerId()).isEqualTo(savedShoppingCart.customerId());
            assertThat(shoppingCart.totalItems().value()).isEqualTo(savedShoppingCart.totalItems().value());
            assertThat(shoppingCart.items()).hasSameSizeAs(savedShoppingCart.items());
            assertThat(shoppingCart.id()).isEqualTo(savedShoppingCart.id());
        });
    }

    @Test
    void shouldUpdateExistingShoppingCart() {
        var shoppingCart = aShoppingCart().withItems(true).build();
        shoppingCarts.add(shoppingCart);

        shoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        var beforeTotalItems  = shoppingCart.totalItems().value();
        var beforeTotalAmount = shoppingCart.totalAmount().value();
        var itemId            = shoppingCart.items().iterator().next().id();

        shoppingCart.changeItemQuantity(itemId, Quantity.of(5));
        shoppingCarts.add(shoppingCart);

        shoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        assertThat(shoppingCart).isNotNull();
        assertThat(shoppingCart.totalItems().value()).isNotEqualTo(beforeTotalItems);
        assertThat(shoppingCart.totalAmount().value()).isNotEqualTo(beforeTotalAmount);
    }

    @Test
    void shouldCountExistingItems() {
        var ShoppingCart = aShoppingCart().build();
        shoppingCarts.add(ShoppingCart);

        assertThat(shoppingCarts.exists(ShoppingCart.id())).isTrue();
        assertThat(shoppingCarts.exists(ShoppingCartId.of())).isFalse();
    }

}
