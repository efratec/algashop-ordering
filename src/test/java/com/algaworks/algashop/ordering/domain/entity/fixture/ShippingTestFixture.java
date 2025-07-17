package com.algaworks.algashop.ordering.domain.entity.fixture;

import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Shipping;

import java.time.LocalDate;

import static com.algaworks.algashop.ordering.domain.entity.fixture.AddressTestFixture.aAddress;
import static com.algaworks.algashop.ordering.domain.entity.fixture.RecipientTestFixture.aRecipient;

public class ShippingTestFixture {

    public static final String MONEY_20 = "20";

    public static Shipping.ShippingBuilder aShipping() {
        return Shipping.builder();
    }

    public static Shipping aShippingWIthDataMinusDay(long value) {
        return Shipping.builder()
                .cost(Money.of(MONEY_20))
                .expectedDate(LocalDate.now().minusDays(value))
                .address(aAddress().build())
                .recipient(aRecipient())
                .build();
    }

    public static Shipping aShippingFull() {
        return Shipping.builder()
                .cost(Money.of(MONEY_20))
                .expectedDate(LocalDate.now())
                .address(aAddress().build())
                .recipient(aRecipient())
                .build();
    }

}
