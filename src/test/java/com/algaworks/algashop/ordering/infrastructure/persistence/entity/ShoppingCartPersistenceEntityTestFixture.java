package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity.ShoppingCartPersistenceEntityBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

import static com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestFixture.existingCustomer;

public class ShoppingCartPersistenceEntityTestFixture {

    public static ShoppingCartPersistenceEntityBuilder existingShoppingCart() {
        return ShoppingCartPersistenceEntity.builder()
                .id(GeneratorId.generateTimeBasedUUID())
                .customer(existingCustomer().build())
                .totalItems(3)
                .totalAmount(new BigDecimal("1250.00"))
                .createdAt(OffsetDateTime.now())
                .items(Set.of(
                        existingItem().build(),
                        existingItemAlt().build()
                ));
    }

    public static ShoppingCartItemPersistenceEntity.ShoppingCartItemPersistenceEntityBuilder existingItem() {
        return ShoppingCartItemPersistenceEntity.builder()
                .id(GeneratorId.generateTimeBasedUUID())
                .price(new BigDecimal(500))
                .quantity(2)
                .available(true)
                .totalAmount(new BigDecimal(1000))
                .name("Notebook")
                .productId(GeneratorId.generateTimeBasedUUID());
    }

    public static ShoppingCartItemPersistenceEntity.ShoppingCartItemPersistenceEntityBuilder existingItemAlt() {
        return ShoppingCartItemPersistenceEntity.builder()
                .id(GeneratorId.generateTimeBasedUUID())
                .price(new BigDecimal(250))
                .quantity(1)
                .available(true)
                .totalAmount(new BigDecimal(250))
                .name("Mouse pad")
                .productId(GeneratorId.generateTimeBasedUUID());
    }

}
