package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.repository.Orders;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

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

    private void persist(Order aggregateRoot) {
        repository.findById(aggregateRoot.id().value().toLong())
                .ifPresentOrElse(
                        existing -> update(aggregateRoot, existing),
                        () -> insert(aggregateRoot)
                );
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
