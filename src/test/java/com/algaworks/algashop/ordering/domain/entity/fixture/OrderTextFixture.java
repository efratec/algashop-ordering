package com.algaworks.algashop.ordering.domain.entity.fixture;

import com.algaworks.algashop.ordering.domain.entity.Order;
import com.algaworks.algashop.ordering.domain.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;

public class OrderTextFixture {

    private CustomerId customerId = CustomerId.generate();

    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;

    private Shipping shipping = aShipping();
    private Billing billing = aBilling();

    private boolean withItems = true;

    private OrderStatusEnum status = OrderStatusEnum.DRAFT;

    private OrderTextFixture() {

    }

    public static OrderTextFixture anOrder() {
        return new OrderTextFixture();
    }

    public Order build() {
        var order = Order.draft(this.customerId);
        order.changeShipping(this.shipping);
        order.changeBilling(this.billing);
        order.changePaymentMethod(this.paymentMethod);

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

    public OrderTextFixture customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTextFixture paymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderTextFixture shipping(Shipping shipping) {
        this.shipping = shipping;
        return this;
    }

    public OrderTextFixture billingInfo(Billing billing) {
        this.billing = billing;
        return this;
    }

    public OrderTextFixture withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public OrderTextFixture status(OrderStatusEnum status) {
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
