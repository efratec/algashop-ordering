package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.*;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.*;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ShoppingCartTestFixture.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class ShoppingCartTest {

    @Test
    void givenCustomer_whenStartShopping_shouldInitializeEmptyCart() {
        var customerId = CustomerId.of();
        ShoppingCart cart = ShoppingCart.startShopping(customerId);
        var event = ShoppingCartCreatedEvent.of(cart.id(), cart.customerId(), cart.createdAt());
        assertWith(cart,
                c -> assertThat(c.id()).isNotNull(),
                c -> assertThat(c.customerId()).isEqualTo(customerId),
                c -> assertThat(c.totalAmount()).isEqualTo(Money.ZERO()),
                c -> assertThat(c.totalItems()).isEqualTo(Quantity.ZERO),
                c -> assertThat(c.isEmpty()).isTrue(),
                c -> assertThat(c.items()).isEmpty(),
                c -> assertThat(c.domainEvents()).contains(event)
        );
    }

    @Test
    void givenEmptyCart_whenAddNewItem_shouldContainItemAndRecalculateTotals() {
        var cart = aShoppingCart().withItems(false).build();
        var product = aProduct().build();

        cart.addItem(product, new Quantity(2));
        assertThat(cart.items()).hasSize(1);

        var item = cart.items().iterator().next();
        assertThat(item.productId()).isEqualTo(product.id());
        assertThat(item.quantity()).isEqualTo(Quantity.of(2));
        assertThat(cart.totalItems()).isEqualTo(Quantity.of(2));
        assertThat(cart.totalAmount()).isEqualTo(Money.of(product.price().value().multiply(new BigDecimal(2))));
    }

    @Test
    void givenCartWithExistingProduct_whenAddSameProduct_shouldIncrementQuantity() {
        var cart = aShoppingCart().withItems(false).build();
        var product = aProduct().build();

        cart.addItem(product, new Quantity(3));
        cart.addItem(product, new Quantity(3));
        var existing = cart.items().iterator().next();

        var items = cart.items();
        assertThat(items).hasSize(1);
        assertThat(existing.quantity()).isEqualTo(new Quantity(6));
    }

    @Test
    void givenCartWithItems_whenRemoveExistingItem_shouldRemoveAndRecalculateTotals() {
        ShoppingCart cart = aShoppingCart().build();
        var item = cart.items().iterator().next();

        cart.removeItem(item.id());

        assertThat(cart.items()).doesNotContain(item);
        assertThat(cart.totalItems()).isEqualTo(
                Quantity.of(cart.items().stream().mapToInt(i -> i.quantity().value()).sum())
        );
    }

    @Test
    void givenCartWithItems_whenRemoveNonexistentItem_shouldThrowShoppingCartDoesNotContainItemException() {
        var cart = aShoppingCart().build();
        var randomId = ShoppingCartItemId.of();

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() -> cart.removeItem(randomId));
    }

    @Test
    void givenCartWithItems_whenEmpty_shouldClearAllItemsAndResetTotals() {
        var cart = aShoppingCart().build();

        cart.empty();

        assertWith(cart,
                c -> Assertions.assertThat(c.isEmpty()).isTrue(),
                c -> Assertions.assertThat(c.totalItems()).isEqualTo(Quantity.ZERO),
                c -> Assertions.assertThat(c.totalAmount()).isEqualTo(Money.ZERO())
        );
    }

    @Test
    void givenCartWithItems_whenChangeItemPrice_shouldRecalculateTotalAmount() {
        var cart = aShoppingCart().withItems(false).build();

        var product = aProduct().build();

        cart.addItem(product, Quantity.of(2));

        product = aProduct()
                .price(Money.of("100"))
                .build();

        cart.refreshItem(product);

        var item = cart.findItem(product.id());

        assertThat(item.price()).isEqualTo(Money.of("100"));
        assertThat(cart.totalAmount()).isEqualTo(Money.of("200"));
    }

    @Test
    void givenCartWithItems_whenDetectUnavailableItems_shouldReturnTrue() {
        var cart = aShoppingCart().build();
        var product = aProduct().inStock(false).build();

        cart.refreshItem(product);

        assertThat(cart.isContainsUnavailableItems()).isTrue();
    }

    @Test
    void givenCartWithItems_whenChangeQuantityToZero_shouldThrowIllegalArgumentException() {
        var cart = aShoppingCart().build();
        var item = cart.items().iterator().next();

        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> cart.changeItemQuantity(item.id(), Quantity.ZERO));
    }

    @Test
    void givenCartWithItems_whenChangeItemQuantity_shouldRecalculateTotalItems() {
        var cart = aShoppingCart().build();
        var item = cart.items().iterator().next();
        cart.changeItemQuantity(item.id(), new Quantity(5));
        assertThat(cart.totalItems()).isEqualTo(
                Quantity.of(cart.items().stream().mapToInt(i -> i.quantity().value()).sum())
        );
    }

    @Test
    void givenCartWithItems_whenFindItemById_shouldReturnItem() {
        var cart = aShoppingCart().build();
        var item = cart.items().iterator().next();
        var found = cart.findItem(item.id());
        assertThat(found).isEqualTo(item);
    }

    @Test
    void givenDifferentIds_whenCompareItems_shouldNotBeEqual() {
        var shoppingCart1 = aShoppingCart().build();
        var shoppingCart2 = aShoppingCart().build();
        assertThat(shoppingCart1).isNotEqualTo(shoppingCart2);
    }

    @Test
    void givenTwoDifferentProducts_whenAddedToCart_shouldContainBothItems() {
        var productA = aProductAltMousePad().build();
        var productB = aProductAltRamMemory().build();

        var shoppingCart = aShoppingCart().withItems(false).build();
        shoppingCart.addItem(productA, Quantity.of(2));
        shoppingCart.addItem(productB, Quantity.of(5));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(shoppingCart.items()).hasSize(2);
            softly.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.of(7));
            softly.assertThat(shoppingCart.totalAmount()).isEqualTo(Money.of("1200"));

            softly.assertThat(shoppingCart.items())
                    .anySatisfy(item -> {
                        softly.assertThat(item.productId()).isEqualTo(productA.id());
                        softly.assertThat(item.quantity()).isEqualTo(Quantity.of(2));
                    });

            softly.assertThat(shoppingCart.items())
                    .anySatisfy(item -> {
                        softly.assertThat(item.productId()).isEqualTo(productB.id());
                        softly.assertThat(item.quantity()).isEqualTo(Quantity.of(5));
                    });
        });
    }

}
