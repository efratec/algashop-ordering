package com.algaworks.algashop.ordering.domain.model.entity.fixture;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;

public class OrderTestFixture {

    private CustomerId customerId = DEFAULT_CUSTOMER_ID;

    private PaymentMethodEnum paymentMethodEnum = PaymentMethodEnum.GATEWAY_BALANCE;

    private Shipping shipping = aShipping();
    private Billing billing = aBilling();

    private boolean withItems = true;

    private OrderStatusEnum status = OrderStatusEnum.DRAFT;

    private OrderTestFixture() {

    }

    public static OrderTestFixture anOrder() {
        return new OrderTestFixture();
    }

    public Order build() {
        var order = Order.draft(this.customerId);
        order.changeShipping(this.shipping);
        order.changeBilling(this.billing);
        order.changePaymentMethod(this.paymentMethodEnum);

        addDefaultsITems(order);
        this.status.applyTransition(order);
        return order;
    }

    public static Billing aBilling() {
        return Billing.builder()
                .address(anAddress())
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .fullName(new FullName("John", "Doe")).build();
    }

    public static Shipping aShipping() {
        return ShippingTestFixture.aShippingFull();
    }

    public static Address anAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .complement("apt. 11")
                .city("Montfort")
                .state("South Carolina")
                .zipCode(ZipCode.of("79911")).build();
    }

    public OrderTestFixture customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTestFixture paymentMethod(PaymentMethodEnum paymentMethodEnum) {
        this.paymentMethodEnum = paymentMethodEnum;
        return this;
    }

    public OrderTestFixture shipping(Shipping shipping) {
        this.shipping = shipping;
        return this;
    }

    public OrderTestFixture billingInfo(Billing billing) {
        this.billing = billing;
        return this;
    }

    public OrderTestFixture withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public OrderTestFixture status(OrderStatusEnum status) {
        this.status = status;
        return this;
    }

    private void addDefaultsITems(Order order) {
        if (this.withItems) {
            order.addItem(ProductTestFixture.aProductAltMousePad().build(),
                    new Quantity(2)
            );

            order.addItem(ProductTestFixture.aProductAltRamMemory().build(),
                    new Quantity(1)
            );
        }
    }


}
