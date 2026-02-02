package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderItem;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.order.Billing;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    public OrderPersistenceEntity fromDomain(Order order) {
        return merge(of(), order);
    }

    public static OrderPersistenceEntity of() {
        return new OrderPersistenceEntity();
    }

    public OrderPersistenceEntity merge(OrderPersistenceEntity orderPersistenceEntity, Order order) {
        orderPersistenceEntity.setId(order.id().value().toLong());
        orderPersistenceEntity.setTotalAmount(order.totalAmount().value());
        orderPersistenceEntity.setTotalItems(order.totalItems().value());
        orderPersistenceEntity.setStatus(order.status().name());
        orderPersistenceEntity.setPaymentMethod(order.paymentMethod().name());
        orderPersistenceEntity.setPlacedAt(order.placedAt());
        orderPersistenceEntity.setPaidAt(order.paidAt());
        orderPersistenceEntity.setCanceledAt(order.canceledAt());
        orderPersistenceEntity.setReadyAt(order.readyAt());
        orderPersistenceEntity.setBilling(convertBillingToEmbeddable(order.billing()));
        orderPersistenceEntity.setShipping(convertShippingToEmbeddable(order.shipping()));
        orderPersistenceEntity.setVersion(order.version()); //Here it is optional
        orderPersistenceEntity.replaceItems(mergeItems(order, orderPersistenceEntity));
        orderPersistenceEntity.setCustomer(customerPersistenceEntityRepository.getReferenceById(order.customerId().value()));
        orderPersistenceEntity.addEvents(order.domainEvents());
        return orderPersistenceEntity;
    }

    private Set<OrderItemPersistenceEntity> mergeItems(Order order, OrderPersistenceEntity orderPersistenceEntity) {
        var newOrUpdatedItems = order.items();

        if (newOrUpdatedItems == null || newOrUpdatedItems.isEmpty()) {
            return new HashSet<>();
        }

        var existingItems = orderPersistenceEntity.getItems();
        if (existingItems == null || existingItems.isEmpty()) {
            return newOrUpdatedItems.stream()
                    .map(this::fromDomain)
                    .collect(Collectors.toSet());
        }

        var existingItemMap = existingItems.stream().collect(Collectors.toMap(OrderItemPersistenceEntity::getId,
                item -> item));

        return newOrUpdatedItems.stream()
                .map(orderItem -> {
                    var itemPersistence = existingItemMap.getOrDefault(
                            orderItem.id().value().toLong(), new OrderItemPersistenceEntity()
                    );
                    return merge(itemPersistence, orderItem);
                }).collect(Collectors.toSet());
    }

    public OrderItemPersistenceEntity fromDomain(OrderItem orderItem) {
        return merge(new OrderItemPersistenceEntity(), orderItem);
    }

    private OrderItemPersistenceEntity merge(OrderItemPersistenceEntity orderItemPersistenceEntity, OrderItem orderItem) {
        orderItemPersistenceEntity.setId(orderItem.id().value().toLong());
        orderItemPersistenceEntity.setProductId(orderItem.productId().value());
        orderItemPersistenceEntity.setProductName(orderItem.productName().value());
        orderItemPersistenceEntity.setQuantity(orderItem.quantity().value());
        orderItemPersistenceEntity.setPrice(orderItem.price().value());
        orderItemPersistenceEntity.setTotalAmount(orderItem.totalAmount().value());
        return orderItemPersistenceEntity;
    }

    public static BillingEmbeddable convertBillingToEmbeddable(Billing billing) {
        if (billing == null) return null;
        return BillingEmbeddable.builder()
                .firstName(billing.fullName().firstName())
                .lastName(billing.fullName().lastName())
                .phone(billing.phone().value())
                .document(billing.document().value())
                .address(convertAddressToEmbeddable(billing.address()))
                .build();
    }

    public static AddressEmbeddable convertAddressToEmbeddable(Address address) {
        if (address == null) return null;
        return AddressEmbeddable.builder()
                .city(address.city())
                .complement(address.complement())
                .street(address.street())
                .state(address.state())
                .neighborhood(address.neighborhood())
                .number(address.number())
                .zipCode(address.zipCode().value())
                .build();
    }

    public static ShippingEmbeddable convertShippingToEmbeddable(Shipping shipping) {
        if (shipping == null) return null;
        return ShippingEmbeddable.builder()
                .expectedDate(shipping.expectedDate())
                .cost(shipping.cost().value())
                .address(convertAddressToEmbeddable(shipping.address()))
                .recipient(convertRecipientToEmbeddable(shipping.recipient()))
                .build();
    }

    private static RecipientEmbeddable convertRecipientToEmbeddable(Recipient recipient) {
        return RecipientEmbeddable.builder()
                .firstName(recipient.fullName().firstName())
                .lastName(recipient.fullName().lastName())
                .document(recipient.document().value())
                .phone(recipient.phone().value())
                .build();
    }

}
