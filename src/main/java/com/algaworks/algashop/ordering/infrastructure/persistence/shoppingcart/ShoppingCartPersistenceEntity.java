package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AbstractAuditableEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static java.util.Objects.requireNonNull;

@Entity
@Data
@ToString(of = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "tb_shopping_cart")
@NoArgsConstructor
public class ShoppingCartPersistenceEntity extends AbstractAuditableEntity<ShoppingCartPersistenceEntity> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private BigDecimal totalAmount;
    private Integer totalItems;

    @JoinColumn
    @ManyToOne(optional = false)
    private CustomerPersistenceEntity customer;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShoppingCartItemPersistenceEntity> items = new LinkedHashSet<>();

    @Builder(toBuilder = true)
    public ShoppingCartPersistenceEntity(UUID id, CustomerPersistenceEntity customer,
                                         BigDecimal totalAmount, Integer totalItems,
                                         OffsetDateTime createdAt,
                                         Set<ShoppingCartItemPersistenceEntity> items) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.createdAt = createdAt;
        this.replaceItems(items);
    }

    public void replaceItems(Set<ShoppingCartItemPersistenceEntity> items) {
        if (items == null || items.isEmpty()) {
            this.setItems(new HashSet<>());
            return;
        }
        requireNonNull(items).forEach(item -> item.setShoppingCart(this));
        items.forEach(this::addItem);
    }

    public void addItem(ShoppingCartItemPersistenceEntity item) {
        if (item == null) {
            return;
        }
        if (this.getItems() == null) {
            this.setItems(new HashSet<>());
        }
        item.setShoppingCart(this);
        this.items.add(item);
    }

    public UUID getCustomerId() {
        if (customer == null) {
            return null;
        }
        return customer.getId();
    }

    public Collection<Object> getEvents() {
        return super.domainEvents();
    }

    public void addEvents(Collection<Object> events) {
        Optional.ofNullable(events).ifPresent(evts -> evts.forEach(this::registerEvent));
    }

}
