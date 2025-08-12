package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceEntityRepository;
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
public class ShoppingCartPersistenceProvider implements ShoppingCarts {

    private final ShoppingCartPersistenceEntityRepository repository;
    private final ShoppingCartPersistenceEntityAssembler assembler;
    private final ShoppingCartPersistenceEntityDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<ShoppingCart> ofId(ShoppingCartId shoppingCartId) {
        return repository.findById(shoppingCartId.value()).map(disassembler::toDomainEntity);
    }

    @Override
    public Optional<ShoppingCart> ofCustomer(CustomerId customerId) {
        return repository.findByCustomer_Id(customerId.value()).map(disassembler::toDomainEntity);
    }

    @Override
    public void remove(ShoppingCart shoppingCart) {
        repository.deleteById(shoppingCart.id().value());
    }

    @Override
    public void remove(ShoppingCartId shoppingCartId) {
        repository.deleteById(shoppingCartId.value());
    }

    @Override
    public boolean exists(ShoppingCartId shoppingCartId) {
        return repository.existsById(shoppingCartId.value());
    }

    @Override
    @Transactional
    public void add(ShoppingCart aggregateRoot) {
        repository.findById(aggregateRoot.id().value())
                .ifPresentOrElse(persistenceEntity ->
                                update(aggregateRoot, persistenceEntity),
                        () -> insert(aggregateRoot)
                );
    }

    @Override
    public Long count() {
        return repository.count();
    }

    private void update(ShoppingCart aggregateRoot, ShoppingCartPersistenceEntity persistenceEntity) {
        persistenceEntity = assembler.merge(persistenceEntity, aggregateRoot);
        entityManager.detach(persistenceEntity);
        persistenceEntity = repository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    private void insert(ShoppingCart aggregateRoot) {
        var persistenceEntity = assembler.fromDomain(aggregateRoot);
        repository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    @SneakyThrows
    private void updateVersion(ShoppingCart aggregateRoot, ShoppingCartPersistenceEntity persistenceEntity) {
        var version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, persistenceEntity.getVersion());
        version.setAccessible(false);
    }

}
