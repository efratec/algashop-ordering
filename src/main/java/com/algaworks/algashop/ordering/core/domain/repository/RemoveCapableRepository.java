package com.algaworks.algashop.ordering.core.domain.repository;

import com.algaworks.algashop.ordering.core.domain.model.AggregateRoot;

public interface RemoveCapableRepository<T extends AggregateRoot<ID>, ID> extends Repository<T, ID> {

    void remove(T t);
    void remove(ID id);

}
