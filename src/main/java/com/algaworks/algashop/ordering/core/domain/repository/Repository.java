package com.algaworks.algashop.ordering.core.domain.repository;

import com.algaworks.algashop.ordering.core.domain.model.AggregateRoot;

import java.util.Optional;

public interface Repository<T extends AggregateRoot<ID>, ID> {

    Optional<T> ofId(ID id);
    boolean exists(ID id);
    void add(T aggregateRoot);
    Long count();

}
