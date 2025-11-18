package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.checkout.BillingData;
import com.algaworks.algashop.ordering.application.checkout.RecipientData;
import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.order.query.CustomerMinimalOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.ShippingData;
import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
        return Order.existing()
                .id(OrderId.from(persistenceEntity.getId()))
                .customerId(CustomerId.from(persistenceEntity.getCustomerId()))
                .totalAmount(Money.of(persistenceEntity.getTotalAmount()))
                .totalItems(Quantity.of(persistenceEntity.getTotalItems()))
                .status(OrderStatusEnum.valueOf(persistenceEntity.getStatus()))
                .paymentMethodEnum(PaymentMethodEnum.valueOf(persistenceEntity.getPaymentMethod()))
                .placedAt(persistenceEntity.getPlacedAt())
                .paidAt(persistenceEntity.getPaidAt())
                .canceledAt(persistenceEntity.getCanceledAt())
                .readyAt(persistenceEntity.getReadyAt())
                .shipping(convertShippingEmbeddableToShipping(persistenceEntity.getShipping()))
                .billing(convertBillingEmbeddableToBilling(persistenceEntity.getBilling()))
                .items(convertOrderItemPersistenceEntityToOrderItem(persistenceEntity.getItems()))
                .version(persistenceEntity.getVersion())
                .build();
    }

    public static Billing convertBillingEmbeddableToBilling(BillingEmbeddable billingEmbeddable) {
        if (billingEmbeddable == null) return null;
        return Billing.builder()
                .fullName(FullName.of(billingEmbeddable.getFirstName(), billingEmbeddable.getLastName()))
                .document(Document.of(billingEmbeddable.getDocument()))
                .phone(Phone.of(billingEmbeddable.getPhone()))
                .address(convertAddressEmbeddableToAddress(billingEmbeddable.getAddress()))
                .email(Email.of(billingEmbeddable.getEmail()))
                .build();
    }

    public static Address convertAddressEmbeddableToAddress(AddressEmbeddable addressEmbeddable) {
        if (addressEmbeddable == null) return null;
        return Address.builder()
                .state(addressEmbeddable.getState())
                .city(addressEmbeddable.getCity())
                .neighborhood(addressEmbeddable.getNeighborhood())
                .street(addressEmbeddable.getStreet())
                .number(addressEmbeddable.getNumber())
                .complement(addressEmbeddable.getComplement())
                .zipCode(ZipCode.of(addressEmbeddable.getZipCode()))
                .build();
    }

    public static Shipping convertShippingEmbeddableToShipping(ShippingEmbeddable shippingEmbeddable) {
        if (shippingEmbeddable == null) return null;
        return Shipping.builder()
                .cost(Money.of(shippingEmbeddable.getCost()))
                .expectedDate(shippingEmbeddable.getExpectedDate())
                .address(convertAddressEmbeddableToAddress(shippingEmbeddable.getAddress()))
                .recipient(convertRecipientToEmbeddable(shippingEmbeddable.getRecipient()))
                .build();
    }

    public static Recipient convertRecipientToEmbeddable(RecipientEmbeddable recipientEmbeddable) {
        if (recipientEmbeddable == null) return null;
        return Recipient.builder()
                .document(Document.of(recipientEmbeddable.getDocument()))
                .phone(Phone.of(recipientEmbeddable.getPhone()))
                .fullName(FullName.of(recipientEmbeddable.getFirstName(), recipientEmbeddable.getLastName()))
                .build();
    }

    public static RecipientData convertRecipientEmbeddableToRecipientData(RecipientEmbeddable recipientEmbeddable) {
        if (recipientEmbeddable == null) return null;
        return RecipientData.builder()
                .firstName(recipientEmbeddable.getFirstName())
                .lastName(recipientEmbeddable.getLastName())
                .document(recipientEmbeddable.getDocument())
                .phone(recipientEmbeddable.getPhone())
                .build();
    }

    public static AddressData convertAddressEmbeddableToAddressData(AddressEmbeddable addressEmbeddable) {
        if (addressEmbeddable == null) return null;
        return AddressData.builder()
                .state(addressEmbeddable.getState())
                .city(addressEmbeddable.getCity())
                .state(addressEmbeddable.getState())
                .zipCode(addressEmbeddable.getZipCode())
                .number(addressEmbeddable.getNumber())
                .complement(addressEmbeddable.getComplement())
                .neighborhood(addressEmbeddable.getNeighborhood())
                .build();
    }

    public static OrderDetailOutput convertOrderPersistenceEntityToOrderDetailOutput(OrderPersistenceEntity orderPersistenceEntity) {
        if (orderPersistenceEntity == null) return null;
        var customer = orderPersistenceEntity.getCustomer();
        var shipping = orderPersistenceEntity.getShipping();
        var address = shipping.getAddress();
        var recipient = orderPersistenceEntity.getShipping().getRecipient();
        var billing = orderPersistenceEntity.getBilling();

        return OrderDetailOutput.builder()
                .id(OrderId.from(orderPersistenceEntity.getId()))
                .customer(CustomerMinimalOutput.builder()
                        .id(customer.getId())
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .email(customer.getEmail())
                        .document(customer.getDocument())
                        .phone(customer.getPhone())
                        .build())
                .totalItems(orderPersistenceEntity.getTotalItems())
                .totalAmount(orderPersistenceEntity.getTotalAmount())
                .placeAt(orderPersistenceEntity.getPlacedAt())
                .paiAt(orderPersistenceEntity.getPaidAt())
                .canceledAt(orderPersistenceEntity.getCanceledAt())
                .readyAt(orderPersistenceEntity.getReadyAt())
                .shipping(ShippingData.builder()
                        .cost(shipping.getCost())
                        .expectedDate(shipping.getExpectedDate())
                        .recipient(convertRecipientEmbeddableToRecipientData(recipient))
                        .address(convertAddressEmbeddableToAddress(address))
                        .build())
                .billing(BillingData.builder()
                        .firstName(billing.getFirstName())
                        .lastName(billing.getLastName())
                        .document(billing.getDocument())
                        .email(billing.getEmail())
                        .phone(billing.getPhone())
                        .address(convertAddressEmbeddableToAddressData(address))
                        .build())
                .build();
    }

    private static Set<OrderItem> convertOrderItemPersistenceEntityToOrderItem(Set<OrderItemPersistenceEntity> orderItemPersistenceEntities) {
        return orderItemPersistenceEntities.stream()
                .map(item -> OrderItem.brandNew()
                        .orderId(OrderId.from(item.getOrderId()))
                        .productId(ProductId.from(item.getProductId()))
                        .productName(ProductName.of(item.getProductName()))
                        .price(Money.of(item.getPrice()))
                        .quantity(Quantity.of(item.getQuantity()))
                        .build())
                .collect(Collectors.toCollection(HashSet::new));
    }

}
