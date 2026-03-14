package com.algaworks.algashop.ordering.core.domain.model.fixture;

import com.algaworks.algashop.ordering.core.domain.model.commons.Address;
import com.algaworks.algashop.ordering.core.domain.model.commons.ZipCode;

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
