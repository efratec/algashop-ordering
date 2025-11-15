package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.order.notification.OrderNotificationApplicationService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.algaworks.algashop.ordering.application.checkout.CheckoutInputTestFixture.aCheckoutInput;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProduct;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ShoppingCartTestFixture.aShoppingCart;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PLACED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CheckoutApplicationServiceTestIT {

    private final CheckoutApplicationService service;
    private final Orders orders;
    private final ShoppingCarts shoppingCarts;
    private final Customers customers;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @MockitoSpyBean
    private OrderNotificationApplicationService orderNotificationApplicationService;

    @MockitoBean
    private ShippingCostService shippingCostService;

    @BeforeEach
    public void setup() {
        when(shippingCostService.calculate(Mockito.any(ShippingCostService.CalculationRequest.class)))
                .thenReturn(new ShippingCostService.CalculationResult(
                        Money.of("10.00"),
                        LocalDate.now().plusDays(3)
                ));

        if (!customers.exists(DEFAULT_CUSTOMER_ID)) {
            customers.add(existingCustomer().build());
        }
    }

    @Test
    void shouldCheckoutOrder_whenCartIsValidAndInStock() {
        var product = aProduct().inStock(true).build();

        var shoppingCart = aShoppingCart().withItems(false).build();
        shoppingCart.addItem(product, Quantity.of(1));
        shoppingCarts.add(shoppingCart);

        var checkoutInput = aCheckoutInput().shoppingCartId(shoppingCart.id().value()).build();

        var orderId = service.checkout(checkoutInput);
        assertThat(orderId).isNotBlank();
        assertThat(orders.exists(OrderId.from(orderId))).isTrue();

        var createdOrder = orders.ofId(OrderId.from(orderId));

        assertThat(createdOrder).isPresent();
        assertThat(createdOrder.get().status()).isEqualTo(PLACED);
        assertThat(createdOrder.get().totalAmount().value()).isGreaterThan(BigDecimal.ZERO);

        Mockito.verify(orderEventListener).listen(Mockito.any(OrderPlacedEvent.class));
        Mockito.verify(orderNotificationApplicationService).notifyNewRegistration(
                Mockito.any(OrderNotificationApplicationService.NotifyNewRegistrationInput.class));

        var shoppingCartUpdated = shoppingCarts.ofId(shoppingCart.id());
        assertThat(shoppingCartUpdated).isPresent();
        assertThat(shoppingCartUpdated.get().isEmpty()).isTrue();
    }

    @Test
    void shouldThrowShoppingCartNotFoundException_whenCheckoutWithNonexistentCart() {
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.checkout(aCheckoutInput().build()));
    }

    @Test
    void shouldThrowEmptyCartException_whenCheckoutWithEmptyCart() {
        var product = aProduct().inStock(true).build();
        var unavailableProduct = aProduct().id(product.id()).inStock(false).build();

        var shoppingCart = aShoppingCart().withItems(false).build();
        shoppingCart.addItem(product, Quantity.of(1));
        shoppingCart.refreshItem(unavailableProduct);
        shoppingCarts.add(shoppingCart);

        var checkoutInput = aCheckoutInput().shoppingCartId(shoppingCart.id().value()).build();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> service.checkout(checkoutInput));
    }

    @Test
    void shouldThrowOutOfStockException_whenCheckoutWithUnavailableItems() {
        var shoppingCart = aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);

        CheckoutInput input = aCheckoutInput()
                .shoppingCartId(shoppingCart.id().value())
                .build();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> service.checkout(input));
    }

}