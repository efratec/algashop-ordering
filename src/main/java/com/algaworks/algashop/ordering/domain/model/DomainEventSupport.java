package com.algaworks.algashop.ordering.domain.model;

import java.util.ArrayList;
import java.util.List;

public class DomainEventSupport {

    private final List<Object> domainEvents = new ArrayList<>();

    public void addEvents(Object event) {
        this.domainEvents.add(event);
    }

    public List<Object> getDomainEvents() {
        return List.copyOf(this.domainEvents);
    }

    public void clearEvents() {
        this.domainEvents.clear();
    }

}

