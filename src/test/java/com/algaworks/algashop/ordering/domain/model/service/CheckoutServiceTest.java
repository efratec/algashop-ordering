package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum.GATEWAY_BALANCE;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture.aBilling;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture.aShipping;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.*;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ShoppingCartTestFixture.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CheckoutServiceTest {

    private final CheckoutService checkoutService = new CheckoutService();

    @Test
    void givenValidShoppingCart_whenCheckout_shouldAddOrderItemPlaceOrderAndEmptyCart() {
        final var customerId = aShoppingCart().customerId;

        final var shoppingCart = ShoppingCart.startShopping(customerId);
        shoppingCart.addItem(aProduct().build(), Quantity.of(2));
        shoppingCart.addItem(aProductAltRamMemory().build(), Quantity.of(1));

        final var expectedCountItemsOrder = shoppingCart.items().size();
        final var expectedTotalItemsOrder = shoppingCart.totalItems();
        final var totalAmountShoppingCart = shoppingCart.totalAmount();

        final var billing = aBilling();
        final var shipping = aShipping();
        final var paymentMethod = GATEWAY_BALANCE;

        final var order = checkoutService.checkout(shoppingCart, aBilling(), aShipping(), paymentMethod);

        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();

        assertThat(order.customerId()).isEqualTo(shoppingCart.customerId());
        assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.billing()).isEqualTo(billing);
        assertThat(order.shipping()).isEqualTo(shipping);
        assertThat(order.isPlaced()).isTrue();

        final var expectedTotalAmountWithChangedShipping = totalAmountShoppingCart.add(shipping.cost());
        assertThat(order.items()).hasSize(expectedCountItemsOrder);
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmountWithChangedShipping);
        assertThat(order.totalItems()).isEqualTo(expectedTotalItemsOrder);

        assertThat(shoppingCart.isEmpty()).isTrue();
        assertThat(Money.ZERO()).isEqualTo(shoppingCart.totalAmount());
        assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
    }

    @Test
    void givenShoppingCartIsEmpty_whenCheckout_shouldThrowException() {
        final var shoppingCart = aShoppingCart().withItems(false).build();
        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(()-> checkoutService.checkout(shoppingCart, aBilling(), aShipping(), GATEWAY_BALANCE));
    }

    @Test
    void givenShoppingCartUnavailables_whenCheckout_shouldThrowException() {
        final var shoppingCart = aShoppingCart().withItems(true).build();
        final var product = aProduct();
        shoppingCart.addItem(product.build(), Quantity.of(2));
        product.inStock(false);
        shoppingCart.refreshItem(product.build());

        assertThat(shoppingCart.isEmpty()).isFalse();
        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(()-> checkoutService.checkout(shoppingCart, aBilling(), aShipping(), GATEWAY_BALANCE));
    }

}