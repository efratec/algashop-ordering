package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository repository;
    private final OrderPersistenceEntityAssembler assembler;
    private final OrderPersistenceEntityDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<Order> ofId(OrderId orderId) {
        return repository.findById(orderId.value().toLong()).map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(OrderId orderId) {
        return repository.existsById(orderId.value().toLong());
    }

    @Override
    @Transactional
    public void add(Order aggregateRoot) {
        persist(aggregateRoot);
    }

    @Override
    public Long count() {
        return repository.count();
    }

    @Override
    public List<Order> placedByCustomerInYear(CustomerId customerId, Year year) {
        return repository.placedByCustomerInYear(customerId.value(), year.getValue())
                .stream().map(disassembler::toDomainEntity).toList();
    }

    @Override
    public long salesQuantityByCustomerInYear(CustomerId customerId, Year year) {
        return repository.salesQuantityByCustomerInYear(customerId.value(), year.getValue());
    }

    @Override
    public Money totalSoldForCustomer(CustomerId customerId) {
        return Money.of(repository.totalSoldForCustomer(customerId.value()));
    }

    private void persist(Order aggregateRoot) {
        repository.findById(aggregateRoot.id().value().toLong())
                .ifPresentOrElse(
                        existing -> update(aggregateRoot, existing),
                        () -> insert(aggregateRoot)
                );
        aggregateRoot.clearDomainEvents();
    }

    private void update(Order aggregateRoot, OrderPersistenceEntity updatedEntity) {
        updatedEntity = assembler.merge(updatedEntity, aggregateRoot);
        entityManager.detach(updatedEntity);
        repository.saveAndFlush(updatedEntity);
        updateVersion(aggregateRoot, updatedEntity);
    }

    private void insert(Order aggregateRoot) {
        var persistenceEntity = assembler.fromDomain(aggregateRoot);
        repository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    @SneakyThrows
    private void updateVersion(Order aggregateRoot, OrderPersistenceEntity persistenceEntity) {
        var version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, persistenceEntity.getVersion());
        version.setAccessible(false);
    }

}
