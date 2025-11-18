package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture.aBilling;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture.aShipping;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProduct;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProductUnavailable;
import static com.algaworks.algashop.ordering.domain.model.order.PaymentMethodEnum.CREDIT_CARD;
import static com.algaworks.algashop.ordering.domain.model.order.PaymentMethodEnum.GATEWAY_BALANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class BuyNowServiceTest {

    private BuyNowService buyNowService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setup() {
        var specification = new CustomerHaveFreeShippingSpecification(orders,
                LoyaltyPoints.of(100),
                2L,
                LoyaltyPoints.of(2000));

        buyNowService = new BuyNowService(specification);
    }

    @Test
    void givenValidBuyNowProduct_when_shouldAddOrderItemAndPlaceOrder() {
        final var product = aProduct().build();
        final var customer = existingCustomer().build();
        final var billing = aBilling();
        final var shipping = aShipping();
        final var quantity = Quantity.of(2);
        final Money totalAmount = product.price().multiply(quantity).add(shipping.cost());

        final var orderBuyNow = buyNowService.buyNow(product, customer,
                billing, shipping, quantity, CREDIT_CARD);

        assertThat(orderBuyNow).isNotNull();
        assertThat(orderBuyNow.customerId()).isEqualTo(customer.id());
        assertThat(orderBuyNow.paymentMethod()).isEqualTo(CREDIT_CARD);
        assertThat(orderBuyNow.billing()).isEqualTo(billing);
        assertThat(orderBuyNow.shipping()).isEqualTo(shipping);
        assertThat(orderBuyNow.totalItems()).isEqualTo(quantity);
        assertThat(orderBuyNow.totalAmount()).isEqualTo(totalAmount);
        assertThat(orderBuyNow.items()).hasSize(1)
                .extracting("productId")
                .containsExactlyInAnyOrder(product.id());
    }

    @Test
    void givenProductUnavailable_whenBuyNow_shouldThrowProductOutOfStockException() {
        final var product = aProductUnavailable().build();
        final var customer = existingCustomer().build();
        final var billing = aBilling();
        final var shipping = aShipping();
        final var quantity = Quantity.of(2);

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> buyNowService.buyNow(product, customer,
                        billing, shipping, quantity, GATEWAY_BALANCE));
    }

    @Test
    void givenInvalidProduct_when_BuyNow_shouldThrowIllegalArgumentException() {
        final var product = aProduct().build();
        final var customer = existingCustomer().build();
        final var billing = aBilling();
        final var shipping = aShipping();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> buyNowService.buyNow(product, customer,
                        billing, shipping, Quantity.ZERO, GATEWAY_BALANCE));
    }

    @Test
    void givenCustomerWithFreeShipping_whenBuyNow_shouldReturnPlacedOrderWithFreeShipping() {
        Mockito.when(orders.salesQuantityByCustomerInYear(
                Mockito.any(CustomerId.class),
                Mockito.any(Year.class)
        )).thenReturn(2L);

        var product = ProductTestFixture.aProduct().build();
        var customer = CustomerTestFixture.existingCustomer().loyaltyPoints(LoyaltyPoints.of(100)).build();
        var billingInfo = OrderTestFixture.aBilling();
        var shippingInfo = OrderTestFixture.aShipping();
        var quantity = Quantity.of(3);
        var paymentMethod = PaymentMethodEnum.CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod);

        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(customer.id());
        assertThat(order.billing()).isEqualTo(billingInfo);
        assertThat(order.shipping()).isEqualTo(shippingInfo.toBuilder().cost(Money.ZERO()).build());
        assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.isPlaced()).isTrue();

        assertThat(order.items()).hasSize(1);
        assertThat(order.items().iterator().next().productId()).isEqualTo(product.id());
        assertThat(order.items().iterator().next().quantity()).isEqualTo(quantity);
        assertThat(order.items().iterator().next().price()).isEqualTo(product.price());

        Money expectedTotalAmount = product.price().multiply(quantity);
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(order.totalItems()).isEqualTo(quantity);
    }

}