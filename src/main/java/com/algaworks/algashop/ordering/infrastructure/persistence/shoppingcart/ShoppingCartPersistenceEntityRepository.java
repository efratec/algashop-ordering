package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartPersistenceEntityRepository extends JpaRepository<ShoppingCartPersistenceEntity, UUID> {

    Optional<ShoppingCartPersistenceEntity> findByCustomer_Id(UUID customerId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE
              ShoppingCartItemPersistenceEntity i
            SET
              i.price = :price,
              i.totalAmount = :price * i.quantity
            WHERE
              i.productId = :productId
            """)
    void updateItemPrice(UUID productId, BigDecimal price);

    @Modifying
    @Transactional
    @Query("""
            UPDATE
              ShoppingCartItemPersistenceEntity i
            SET
              i.available = :availability
            WHERE
              i.productId = :productId
            """)
    void updateItemAvailability(UUID productId, boolean availability);

    @Modifying
    @Transactional
    @Query("""
            UPDATE
              ShoppingCartPersistenceEntity sc
            SET
              sc.totalAmount = (
                SELECT SUM(i.totalAmount)
                  FROM ShoppingCartItemPersistenceEntity i
                  WHERE i.shoppingCart.id = sc.id
                )
            WHERE
              EXISTS (SELECT 1
                  FROM ShoppingCartItemPersistenceEntity i2
                  WHERE i2.shoppingCart.id = sc.id
                  AND i2.productId = :productId)
            """)
    void recalculateTotalsForCartsWithProduct(UUID productId);

}
