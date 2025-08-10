package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
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
                .build();
    }

    private static Set<OrderItem> convertOrderItemPersistenceEntityToOrderItem(Set<OrderItemPersistenceEntity> orderItemPersistenceEntities) {
        return orderItemPersistenceEntities.stream()
                .map(item -> OrderItem.brandNew()
                .orderId(OrderId.from(item.getOrderId()))
                .product(Product.of(ProductId.from(item.getProductId()),
                        ProductName.of(item.getProductName()),
                        Money.of(item.getPrice()),
                        null
                ))
                .quantity(Quantity.of(item.getQuantity()))
                .build())
                .collect(Collectors.toCollection(HashSet::new));
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

    private static Recipient convertRecipientToEmbeddable(RecipientEmbeddable recipientEmbeddable) {
        if (recipientEmbeddable == null) return null;
        return Recipient.builder()
                .document(Document.of(recipientEmbeddable.getDocument()))
                .phone(Phone.of(recipientEmbeddable.getPhone()))
                .fullName(FullName.of(recipientEmbeddable.getFirstName(), recipientEmbeddable.getLastName()))
                .build();
    }

}
