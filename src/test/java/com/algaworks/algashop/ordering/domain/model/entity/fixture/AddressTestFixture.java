package com.algaworks.algashop.ordering.domain.model.entity.fixture;

import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;

public class AddressTestFixture {

    public static Address.AddressBuilder aAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .number("1134")
                .neighborhood("North Ville")
                .city("York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .complement("Apt. 114")
                .build().toBuilder();
    }

}
