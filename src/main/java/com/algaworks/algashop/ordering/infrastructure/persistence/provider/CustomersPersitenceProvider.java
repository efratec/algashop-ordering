package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
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
public class CustomersPersitenceProvider implements Customers {

    private final CustomerPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityAssembler assembler;
    private final CustomerPersistenceEntityDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<Customer> ofId(CustomerId customerId) {
        return repository.findById(customerId.value()).map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(CustomerId customerId) {
        return repository.existsById(customerId.value());
    }

    @Override
    @Transactional(readOnly = false)
    public void add(Customer aggregateRoot) {
        persist(aggregateRoot);
    }

    @Override
    public Long count() {
        return repository.count();
    }

    private void persist(Customer aggregateRoot) {
        repository.findById(aggregateRoot.id().value())
                .ifPresentOrElse(existing -> update(aggregateRoot, existing),
                        () -> insert(aggregateRoot));
    }

    private void insert(Customer aggregateRoot) {
        var persitenceEntity = assembler.fromDomain(aggregateRoot);
        repository.saveAndFlush(persitenceEntity);
        updateVersion(aggregateRoot, persitenceEntity);
    }

    private void update(Customer aggregateRoot, CustomerPersistenceEntity updatedEntity) {
        updatedEntity = assembler.merge(updatedEntity, aggregateRoot);
        entityManager.detach(updatedEntity);
        repository.saveAndFlush(updatedEntity);
        updateVersion(aggregateRoot, updatedEntity);
    }

    @SneakyThrows
    private void updateVersion(Customer aggregateRoot, CustomerPersistenceEntity persistenceEntity) {
        var version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, persistenceEntity.getVersion());
        version.setAccessible(false);
    }


}
