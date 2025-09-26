package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;
import io.hypersistence.tsid.TSID;

import java.util.Objects;

public record OrderId(TSID value) {

    public OrderId {
        Objects.requireNonNull(value);
    }

    public static OrderId of() {
        return new OrderId(GeneratorId.gererateTSID());
    }

    public static OrderId from(Long value) {
        return new OrderId(TSID.from(value));
    }

    public static OrderId from(String value) {
        return new OrderId(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
