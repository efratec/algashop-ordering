package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.AbstractEventSourceEntity;
import com.algaworks.algashop.ordering.domain.model.AggregateRoot;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.*;
import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.requireAllNonNull;
import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.validate;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.CollectionUtils.isEmpty;


@EqualsAndHashCode(of = "id", callSuper = true)
public class Order extends AbstractEventSourceEntity implements AggregateRoot<OrderId> {

    private OrderId id;
    private CustomerId customerId;

    private Money totalAmount;
    private Quantity totalItems;

    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;

    private Billing billing;
    private Shipping shipping;

    private OrderStatusEnum status;
    private PaymentMethodEnum paymentMethodEnum;

    private Long version;

    private Set<OrderItem> items;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    public Order(OrderId id, CustomerId customerId,
                 Money totalAmount, Quantity totalItems,
                 OffsetDateTime placedAt, OffsetDateTime paidAt,
                 OffsetDateTime canceledAt, OffsetDateTime readyAt,
                 Billing billing, Shipping shipping,
                 OrderStatusEnum status, PaymentMethodEnum paymentMethodEnum,
                 Set<OrderItem> items, Long version) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCanceledAt(canceledAt);
        this.setReadyAt(readyAt);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setStatus(status);
        this.setPaymentMethodEnum(paymentMethodEnum);
        this.setItems(items);
        this.setVersion(version);
    }

    public static Order draft(CustomerId customerId) {
        return new Order(
                OrderId.of(),
                customerId,
                Money.ZERO(),
                Quantity.ZERO,
                null,
                null,
                null,
                null,
                null,
                null,
                OrderStatusEnum.DRAFT,
                null,
                new HashSet<>(),
                null
        );
    }

    public void addItem(Product product, Quantity quantity) {
        requireAllNonNull("Product", product, "Quantity", quantity);
        verifyIfChangeable();

        product.checkoutOfStock();

        var orderItem = OrderItem.brandNew()
                .orderId(this.id())
                .quantity(quantity)
                .productId(product.id())
                .productName(product.name())
                .price(product.price())
                .build();

        if (this.items == null) {
            this.items = new HashSet<>();
        }

        this.items.add(orderItem);
        this.recalculateTotals();
    }

    public void removeItem(OrderItemId orderItemId) {
        requireNonNull(orderItemId);
        verifyIfChangeable();
        var orderItem = findOrderItem(orderItemId);
        this.items.remove(orderItem);
        this.recalculateTotals();
    }

    public void cancel() {
        this.changeStatus(OrderStatusEnum.CANCELED);
        this.setCanceledAt(OffsetDateTime.now());
        this.publishDomainEvent(OrderCanceledEvent.of(id(), customerId(), this.canceledAt()));
    }

    public void place() {
        verifyIfCanChangeToPlaced();
        validate(() -> this.items().isEmpty(), NO_ITEMS, OrderCannotBePlacedException::new, this.id());
        this.changeStatus(OrderStatusEnum.PLACED);
        this.setPlacedAt(OffsetDateTime.now());
        this.publishDomainEvent(OrderPlacedEvent.of(this.id(), this.customerId(), this.placedAt()));
    }

    public void markAsPaid() {
        this.changeStatus(OrderStatusEnum.PAID);
        this.setPaidAt(OffsetDateTime.now());
        this.publishDomainEvent(OrderPaidEvent.of(this.id(), this.customerId(), this.paidAt()));
    }

    public void markAsReady() {
        this.changeStatus(OrderStatusEnum.READY);
        this.setReadyAt(OffsetDateTime.now());
        this.publishDomainEvent(OrderReadyEvent.of(this.id(), this.customerId(), this.readyAt()));
    }

    public void changePaymentMethod(PaymentMethodEnum paymentMethodEnum) {
        requireNonNull(paymentMethodEnum);
        verifyIfChangeable();
        this.setPaymentMethodEnum(paymentMethodEnum);
    }

    public void changeBilling(Billing billing) {
        requireNonNull(billing);
        verifyIfChangeable();
        this.setBilling(billing);
    }

    public void changeShipping(Shipping newShipping) {
        requireNonNull(newShipping);
        verifyIfChangeable();
        validate(() -> newShipping.expectedDate().isBefore(LocalDate.now()),
                NO_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST, OrderInvalidShippingDeliveryDateException::new,
                this.id());
        this.setShipping(newShipping);
    }

    public void changeItemQuantity(OrderItemId orderItemId, Quantity quantity) {
        requireAllNonNull("OrderItemId", orderItemId, "Quantity", quantity);
        verifyIfChangeable();

        var orderItem = findOrderItem(orderItemId);
        orderItem.changeQuantity(quantity);
        this.recalculateTotals();
    }

    public boolean isDraft() {
        return OrderStatusEnum.DRAFT.equals(this.status());
    }

    public boolean isReady() {
        return OrderStatusEnum.READY.equals(this.status());
    }

    public boolean isCanceled() {
        return OrderStatusEnum.CANCELED.equals(this.status());
    }

    public boolean isPaid() {
        return OrderStatusEnum.PAID.equals(this.status());
    }

    public boolean isPlaced() {
        return OrderStatusEnum.PLACED.equals(this.status());
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public OffsetDateTime placedAt() {
        return placedAt;
    }

    public OffsetDateTime paidAt() {
        return paidAt;
    }

    public OffsetDateTime canceledAt() {
        return canceledAt;
    }

    public OffsetDateTime readyAt() {
        return readyAt;
    }

    public Billing billing() {
        return billing;
    }

    public Shipping shipping() {
        return shipping;
    }

    public OrderStatusEnum status() {
        return status;
    }

    public PaymentMethodEnum paymentMethod() {
        return paymentMethodEnum;
    }

    public Set<OrderItem> items() {
        return Collections.unmodifiableSet(this.items);
    }

    public Long version() {
        return version;
    }

    private void recalculateTotals() {
        var totalItemsAmount = this.items().stream().map(i -> i.totalAmount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItemsQuantity = this.items().stream().map(i -> i.quantity().value())
                .reduce(0, Integer::sum);

        BigDecimal shippingCost;
        if (this.shipping() == null) {
            shippingCost = BigDecimal.ZERO;
        } else {
            shippingCost = this.shipping().cost().value();
        }

        var amountTotal = totalItemsAmount.add(shippingCost);
        this.setTotalAmount(Money.of(amountTotal));
        this.setTotalItems(Quantity.of(totalItemsQuantity));
    }

    private void changeStatus(OrderStatusEnum newStatus) {
        requireNonNull(newStatus);
        validate(() -> this.status().canNotChangeTo(newStatus), NO_ORDER_STATUS_CANNOT_BE_CHANGED,
                OrderStatusCannotBeChangedException::new, this.id(), this.status(), newStatus);
        this.setStatus(newStatus);
    }

    private void verifyIfChangeable() {
        validate(() -> !isDraft(), NO_ORDER_CANNOT_BE_EDITTED, OrderCannotBeEditedException::new, this.id(), this.status());
    }

    private void verifyIfCanChangeToPlaced() {
        validate(() -> isNull(this.shipping()), NO_SHIPPING_INFO, OrderCannotBePlacedException::new, this.id());
        validate(() -> isNull(this.billing()), NO_BILLING_INFO, OrderCannotBePlacedException::new, this.id());
        validate(() -> isNull(this.paymentMethod()), NO_PAYMENT_METHOD, OrderCannotBePlacedException::new, this.id());
        validate(() -> isEmpty(this.items()), NO_ITEMS, OrderCannotBePlacedException::new, this.id());
    }

    private OrderItem findOrderItem(OrderItemId orderItemId) {
        return this.items().stream().filter(i -> i.id().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> OrderDoesNotContainOrderItemException.because(this.id(), orderItemId));
    }

    private void setId(OrderId id) {
        requireNonNull(id);
        this.id = id;
    }

    private void setCustomerId(CustomerId customerId) {
        requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setTotalAmount(Money totalAmount) {
        requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(Quantity totalItems) {
        requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setPlacedAt(OffsetDateTime placedAt) {
        this.placedAt = placedAt;
    }

    private void setPaidAt(OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    private void setCanceledAt(OffsetDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    private void setReadyAt(OffsetDateTime readyAt) {
        this.readyAt = readyAt;
    }

    private void setBilling(Billing billing) {
        this.billing = billing;
    }

    private void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    private void setStatus(OrderStatusEnum status) {
        requireNonNull(status);
        this.status = status;
    }

    private void setPaymentMethodEnum(PaymentMethodEnum paymentMethodEnum) {
        this.paymentMethodEnum = paymentMethodEnum;
    }

    private void setItems(Set<OrderItem> items) {
        requireNonNull(items);
        this.items = items;
    }

    private void setVersion(Long version) {
        this.version = version;
    }

}
