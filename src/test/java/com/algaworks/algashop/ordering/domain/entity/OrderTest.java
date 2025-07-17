package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.entity.fixture.OrderTextFixture;
import com.algaworks.algashop.ordering.domain.entity.fixture.ProductTestFixture;
import com.algaworks.algashop.ordering.domain.entity.fixture.ShippingTestFixture;
import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.algaworks.algashop.ordering.domain.entity.fixture.ProductTestFixture.aProductAltMousePad;
import static com.algaworks.algashop.ordering.domain.entity.fixture.ProductTestFixture.aProductUnavailable;
import static com.algaworks.algashop.ordering.domain.entity.fixture.ShippingTestFixture.aShippingFull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderTest {

    public static final Quantity QUANTITY_1 = Quantity.of(1);
    public static final Quantity QUANTITY_2 = Quantity.of(2);
    public static final Quantity QUANTITY_3 = Quantity.of(3);
    public static final Quantity QUANTITY_5 = Quantity.of(5);
    public static final CustomerId GENERATE_CUSTOMER_ID = CustomerId.generate();

    @Test
    void shouldGenerate() {
        final var order = Order.draft(GENERATE_CUSTOMER_ID);
        assertNotNull(order);
    }

    @Test
    void shouldAddItem() {
        final var order = Order.draft(GENERATE_CUSTOMER_ID);
        final var product = ProductTestFixture.aProductAltMousePad().build();
        final var productId = product.id();

        order.addItem(product, QUANTITY_1);

        Assertions.assertThat(order.items()).hasSize(1);

        final var orderItem = order.items().iterator().next();

        Assertions.assertWith(orderItem,
                i -> Assertions.assertThat(i.id()).isNotNull(),
                i -> Assertions.assertThat(i.productName()).isEqualTo(ProductName.of("Mouse Pad")),
                i -> Assertions.assertThat(i.productId()).isEqualTo(productId),
                i -> Assertions.assertThat(i.price()).isEqualTo(Money.of("100")),
                i -> Assertions.assertThat(i.quantity()).isEqualTo(QUANTITY_1)
        );
    }

    @Test
    void shouldGenerateExceptionWhenTryToChangeItemSet() {
        var order = Order.draft(GENERATE_CUSTOMER_ID);
        order.addItem(aProductAltMousePad().build(), QUANTITY_1);

        var items = order.items();

        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(items::clear);
    }

    @Test
    void shouldCalculateTotals() {
        var order = Order.draft(GENERATE_CUSTOMER_ID);
        order.addItem(ProductTestFixture.aProductAltMousePad().build(), QUANTITY_2);
        order.addItem(ProductTestFixture.aProductAltRamMemory().build(), QUANTITY_1);
        Assertions.assertThat(order.totalAmount()).isEqualTo(Money.of("400"));
        Assertions.assertThat(order.totalItems()).isEqualTo(QUANTITY_3);
    }

    @Test
    void givenDraftOrder_whenPlace_shouldChangeToPlaced() {
        var order = OrderTextFixture.anOrder().build();
        order.place();
        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenPlacedOrder_whenTryToPlace_shouldGenerateException() {
       var order = OrderTextFixture.anOrder().status(OrderStatusEnum.PLACED).build();
        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::place);
    }

    @Test
    void givenDraftOrder_whenChangePaymentMethod_shouldAllowChange() {
        var order = Order.draft(GENERATE_CUSTOMER_ID);
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        Assertions.assertWith(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void givenDraftOrder_whenChangeBillingInfo_shouldAllowChange() {
        var address = Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .complement("apt. 11")
                .city("Montfort")
                .state("South Carolina")
                .zipCode(ZipCode.of("79911")).build();

        var billingInfo = Billing.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .fullName(new FullName("John", "Doe"))
                .build();

        Order order = Order.draft(GENERATE_CUSTOMER_ID);
        order.changeBilling(billingInfo);

        Billing expectedBilling = Billing.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .fullName(new FullName("John", "Doe"))
                .build();

        Assertions.assertThat(order.billing()).isEqualTo(expectedBilling);
    }

    @Test
    void givenDraftOrder_whenChangeShipping_shouldAllowChange() {
        var shipping = aShippingFull();
        Order order = Order.draft(GENERATE_CUSTOMER_ID);
        order.changeShipping(shipping);

        Assertions.assertWith(order, o -> Assertions.assertThat(o.shipping()).isEqualTo(shipping));
    }

    @Test
    void givenDraftOrderAndDeliveryDateInThePast_whenChangeShipping_shouldNotAllowChange() {
        var order = Order.draft(GENERATE_CUSTOMER_ID);
        var shipping = ShippingTestFixture.aShippingWIthDataMinusDay(2);

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(()-> order.changeShipping(shipping));
    }

    @Test
    void givenDraftOrder_whenChangeItem_shouldRecalculate() {
        var order = Order.draft(GENERATE_CUSTOMER_ID);
        order.addItem(aProductAltMousePad().build(), QUANTITY_3);

        var orderItem = order.items().iterator().next();

        order.changeItemQuantity(orderItem.id(), QUANTITY_5);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.totalAmount()).isEqualTo(Money.of("500")),
                o -> Assertions.assertThat(o.totalItems()).isEqualTo(QUANTITY_5)
        );
    }

    @Test
    void givenOutOfStockProduct_whenTryToAddOrder_should_NotAllow() {
        var order = Order.draft(GENERATE_CUSTOMER_ID);
        ThrowableAssert.ThrowingCallable addItemTask = () -> order.addItem(aProductUnavailable().build(),  QUANTITY_1 );
        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class).isThrownBy(addItemTask);
    }

    @Test
    void givenDraftOrder_whenChangeBilling_shouldAllowChange() {
        Billing billing = OrderTextFixture.aBilling();
        Order order = Order.draft(CustomerId.generate());
        order.changeBilling(billing);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
    }

    //void givenDraftORder_when_Change

}
