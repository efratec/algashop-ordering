package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.ShoppingCartPersistenceProvider;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ShoppingCartTestFixture.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ShoppingCartPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShoppingCartsIT {

    private final ShoppingCarts shoppingCarts;
    private final Customers customers;
    private final EntityManager entityManager;

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
        assertThat(shoppingCarts.count()).isZero();

        var cartT1 = aShoppingCart().build();
        var cartT2 = aShoppingCart().build();

        shoppingCarts.add(cartT1);
        shoppingCarts.add(cartT2);

        assertThat(shoppingCarts.count()).isEqualTo(2L);
    }

}
