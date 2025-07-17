package com.algaworks.algashop.ordering.domain.valueobject.id;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;
import io.hypersistence.tsid.TSID;

import java.util.Objects;

public record OrderItemId(TSID value) {

    public OrderItemId {
        Objects.requireNonNull(value);
    }

    public static OrderItemId of() {
        return new OrderItemId(GeneratorId.gererateTSID());
    }

    public static OrderItemId of(String value) {
        return new OrderItemId(TSID.from(value));
    }

    public static OrderItemId of(Long value) {
        return new OrderItemId(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
