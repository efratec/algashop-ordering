package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture.aBilling;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture.aShipping;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProduct;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProductAltRamMemory;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ShoppingCartTestFixture.aShoppingCart;
import static com.algaworks.algashop.ordering.domain.model.order.PaymentMethodEnum.GATEWAY_BALANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    private CheckoutService checkoutService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setup() {
        var specification = new CustomerHaveFreeShippingSpecification(orders,
                LoyaltyPoints.of(100),
                2L,
                LoyaltyPoints.of(2000));

        checkoutService = new CheckoutService(specification);
    }

    @Test
    void givenValidShoppingCart_whenCheckout_shouldAddOrderItemPlaceOrderAndEmptyCart() {
        final var customer = CustomerTestFixture.existingCustomer().build();

        final var shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCart.addItem(aProduct().build(), Quantity.of(2));
        shoppingCart.addItem(aProductAltRamMemory().build(), Quantity.of(1));

        final var expectedCountItemsOrder = shoppingCart.items().size();
        final var expectedTotalItemsOrder = shoppingCart.totalItems();
        final var totalAmountShoppingCart = shoppingCart.totalAmount();

        final var billing = aBilling();
        final var shipping = aShipping();
        final var paymentMethod = GATEWAY_BALANCE;

        final var order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

        final var event = OrderPlacedEvent.of(order.id(), order.customerId(), order.placedAt());
        assertThat(order.domainEvents()).contains(event);

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
        final var customer = CustomerTestFixture.existingCustomer().build();
        final var shoppingCart = aShoppingCart().customerId(customer.id()).withItems(false).build();
        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(()-> checkoutService.checkout(customer, shoppingCart, aBilling(), aShipping(), GATEWAY_BALANCE));
    }

    @Test
    void givenShoppingCartUnavailables_whenCheckout_shouldThrowException() {
        final var customer = CustomerTestFixture.existingCustomer().build();
        final var shoppingCart = aShoppingCart().customerId(customer.id()).withItems(true).build();
        final var product = aProduct();
        shoppingCart.addItem(product.build(), Quantity.of(2));
        product.inStock(false);
        shoppingCart.refreshItem(product.build());

        assertThat(shoppingCart.isEmpty()).isFalse();
        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(()-> checkoutService.checkout(customer, shoppingCart, aBilling(), aShipping(), GATEWAY_BALANCE));
    }

    @Test
    void givenValidShoppingCartAndCustomerWithFreeShipping_whenCheckout_shouldReturnPlacedOrderWithFreeShipping() {
        var customer = CustomerTestFixture.existingCustomer().loyaltyPoints(new LoyaltyPoints(3000)).build();

        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCart.addItem(ProductTestFixture.aProduct().build(), new Quantity(2));
        shoppingCart.addItem(ProductTestFixture.aProductAltRamMemory().build(), new Quantity(1));


        var billingInfo = OrderTestFixture.aBilling();
        var shippingInfo = OrderTestFixture.aShipping();
        var paymentMethod = PaymentMethodEnum.CREDIT_CARD;

        Money shoppingCartTotalAmount = shoppingCart.totalAmount();
        Quantity expectedOrderTotalItems = shoppingCart.totalItems();
        int expectedOrderItemsCount = shoppingCart.items().size();

        Order order = checkoutService.checkout(customer, shoppingCart, billingInfo, shippingInfo, paymentMethod);

        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(shoppingCart.customerId());
        assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.billing()).isEqualTo(billingInfo);
        assertThat(order.shipping()).isEqualTo(shippingInfo.toBuilder().cost(Money.ZERO()).build());
        assertThat(order.isPlaced()).isTrue();

        assertThat(order.totalAmount()).isEqualTo(shoppingCartTotalAmount);
        assertThat(order.totalItems()).isEqualTo(expectedOrderTotalItems);
        assertThat(order.items()).hasSize(expectedOrderItemsCount);

        assertThat(shoppingCart.isEmpty()).isTrue();
        assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO());
        assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
    }

}