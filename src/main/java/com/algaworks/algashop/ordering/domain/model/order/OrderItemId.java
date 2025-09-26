package com.algaworks.algashop.ordering.domain.model.order;

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

    public static OrderItemId from(String value) {
        return new OrderItemId(TSID.from(value));
    }

    public static OrderItemId from(Long value) {
        return new OrderItemId(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
