package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.util.Objects;

public record BillingInfo(FullName fullName,
                          Document document,
                          Phone phone,
                          Address address) {

    @Builder(toBuilder = true)
    public BillingInfo {
        Objects.requireNonNull(fullName, "fullName should not be blank");
        Objects.requireNonNull(document, "document should not be blank");
        Objects.requireNonNull(phone, "phone should not be blank");
        Objects.requireNonNull(address, "address should not be blank");
    }

    public static BillingInfo of(FullName fullName, Document document, Phone phone, Address address) {
        return new BillingInfo(fullName, document, phone, address);
    }

}
