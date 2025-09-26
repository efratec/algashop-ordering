package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShoppingCartPersistenceEntityDisassembler {

    private ShoppingCartPersistenceEntityDisassembler() {}

    public static ShoppingCartPersistenceEntityDisassembler of() {
        return new ShoppingCartPersistenceEntityDisassembler();
    }

    public ShoppingCart toDomainEntity(ShoppingCartPersistenceEntity cartPersistenceEntity) {
        return ShoppingCart.existing()
                .id(ShoppingCartId.from(cartPersistenceEntity.getId()))
                .customerId(CustomerId.from(cartPersistenceEntity.getCustomerId()))
                .totalAmount(Money.of(cartPersistenceEntity.getTotalAmount()))
                .createdAt(cartPersistenceEntity.getCreatedAt())
                .totalItems(Quantity.of(cartPersistenceEntity.getTotalItems()))
                .items(toItemsDomainEntities(cartPersistenceEntity.getItems()))
                .version(cartPersistenceEntity.getVersion())
                .build();
    }

    private Set<ShoppingCartItem> toItemsDomainEntities(Set<ShoppingCartItemPersistenceEntity> items) {
        return items.stream().map(this::convertToItemEntity).collect(Collectors.toSet());
    }

    private ShoppingCartItem convertToItemEntity(ShoppingCartItemPersistenceEntity cartItemPersistenceEntity) {
        return ShoppingCartItem.existing()
                .id(ShoppingCartItemId.from(cartItemPersistenceEntity.getId()))
                .shoppingCartId(ShoppingCartId.from(cartItemPersistenceEntity.getShoppingCartId()))
                .productId(ProductId.from(cartItemPersistenceEntity.getProductId()))
                .productName(ProductName.of(cartItemPersistenceEntity.getName()))
                .price(Money.of(cartItemPersistenceEntity.getPrice()))
                .quantity(Quantity.of(cartItemPersistenceEntity.getQuantity()))
                .available(cartItemPersistenceEntity.getAvailable())
                .totalAmount(new Money(cartItemPersistenceEntity.getTotalAmount()))
                .build();
    }


}
