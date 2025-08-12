package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
        return merge(new ShoppingCartPersistenceEntity(), shoppingCart);
    }

    public ShoppingCartItemPersistenceEntity fromDomain(ShoppingCartItem shoppingCartItem) {
        return mergeItem(new ShoppingCartItemPersistenceEntity(), shoppingCartItem);
    }

    public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity persistenceEntity,
                                               ShoppingCart shoppingCart) {
        persistenceEntity.setId(shoppingCart.id().value());
        persistenceEntity.setCustomer(customerPersistenceEntityRepository.getReferenceById(shoppingCart.customerId().value()));
        persistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        persistenceEntity.setCreatedAt(shoppingCart.createdAt());
        persistenceEntity.replaceItems(mergeItems(shoppingCart, persistenceEntity));
        persistenceEntity.setTotalItems(shoppingCart.totalItems().value());
        persistenceEntity.setVersion(shoppingCart.version());
        return persistenceEntity;
    }

    private Set<ShoppingCartItemPersistenceEntity> mergeItems(ShoppingCart shoppingCart,
                                                              ShoppingCartPersistenceEntity cartPersistenceEntity) {

        var newOrUpdatedShoppingItems = shoppingCart.items();

        if (newOrUpdatedShoppingItems == null || newOrUpdatedShoppingItems.isEmpty()) {
            return new LinkedHashSet<>();
        }

        var existingShoppingItems = cartPersistenceEntity.getItems();
        if (existingShoppingItems == null || existingShoppingItems.isEmpty()) {
            return newOrUpdatedShoppingItems.stream()
                    .map(this::fromDomain)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        var existingItemMap = existingShoppingItems.stream().collect(Collectors.toMap(ShoppingCartItemPersistenceEntity::getId,
                item -> item));

        return newOrUpdatedShoppingItems.stream()
                .map(shoppingCartItem -> {
                    var itemPersistence = existingItemMap.getOrDefault(
                            shoppingCartItem.id().value(), new ShoppingCartItemPersistenceEntity()
                    );
                    return mergeItem(itemPersistence, shoppingCartItem);
                }).collect(Collectors.toSet());
    }

  /*  private ShoppingCartItemPersistenceEntity mergeItem(ShoppingCartItemPersistenceEntity persistenceItemEntity,
                                                        ShoppingCartItem shoppingCartItem) {

        persistenceItemEntity.setId(shoppingCartItem.id().value());
        persistenceItemEntity.setProductId(shoppingCartItem.productId().value());
        persistenceItemEntity.setName(shoppingCartItem.productName().value());
        persistenceItemEntity.setPrice(shoppingCartItem.price().value());
        persistenceItemEntity.setQuantity(shoppingCartItem.quantity().value());
        persistenceItemEntity.setAvailable(shoppingCartItem.isAvailable());
        persistenceItemEntity.setTotalAmount(shoppingCartItem.totalAmount().value());
        persistenceItemEntity.setShoppingCart(ShoppingCartPersistenceEntity.builder()
                .id(shoppingCartItem.id().value()).build());
        return persistenceItemEntity;
    }*/

    private ShoppingCartItemPersistenceEntity mergeItem(ShoppingCartItemPersistenceEntity shoppingCartItemPersistenceEntity,
                                                    ShoppingCartItem shoppingCartItem) {

        shoppingCartItemPersistenceEntity.setId(shoppingCartItem.id().value());
        shoppingCartItemPersistenceEntity.setName(shoppingCartItem.productName().value());
        shoppingCartItemPersistenceEntity.setProductId(shoppingCartItem.productId().value());
        shoppingCartItemPersistenceEntity.setAvailable(shoppingCartItem.isAvailable());
        shoppingCartItemPersistenceEntity.setQuantity(shoppingCartItem.quantity().value());
        shoppingCartItemPersistenceEntity.setPrice(shoppingCartItem.price().value());
        shoppingCartItemPersistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());
        return shoppingCartItemPersistenceEntity;
    }

}
