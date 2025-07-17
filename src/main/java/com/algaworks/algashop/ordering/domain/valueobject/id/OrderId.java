package com.algaworks.algashop.ordering.domain.valueobject.id;

import com.algaworks.algashop.ordering.domain.utility.GeneratorId;
import io.hypersistence.tsid.TSID;

import java.util.Objects;

public record OrderId(TSID value) {

    public OrderId {
        Objects.requireNonNull(value);
    }

    public static OrderId generate() {
        return new OrderId(GeneratorId.gererateTSID());
    }

    public static OrderId of(Long value) {
        return new OrderId(TSID.from(value));
    }

    public static OrderId of(String value) {
        return new OrderId(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
