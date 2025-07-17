package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.algaworks.algashop.ordering.domain.entity.fixture.AddressTestFixture.aAddress;
import static com.algaworks.algashop.ordering.domain.entity.fixture.RecipientTestFixture.aRecipient;
import static com.algaworks.algashop.ordering.domain.entity.fixture.ShippingTestFixture.MONEY_20;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShippingTest {

    @Test
    void givenValidData_whenCreate_thenShouldCreateShipping() {
        final var cost = Money.of(MONEY_20);
        final var expectedDate = LocalDate.now();
        final var recipient = aRecipient();
        final var address = aAddress().build();

        final var shippingInfoExpected = Shipping.of(cost, expectedDate, recipient, address);

        assertEquals(cost, shippingInfoExpected.cost());
        assertEquals(recipient, shippingInfoExpected.recipient());
        assertEquals(address, shippingInfoExpected.address());
    }

    @Test
    void givenNullCost_whenCreate_thenShouldThrowException() {
        final var expectedDate = LocalDate.now();
        final var recipient = aRecipient();
        final var address = aAddress().build();

        assertThrows(NullPointerException.class, () ->
                Shipping.of(null, expectedDate, recipient, address)
        );
    }

    @Test
    void givenNullExpectedDate_whenCreate_thenShouldThrowException() {
        final var cost = Money.of(MONEY_20);
        final var recipient = aRecipient();
        final var address = aAddress().build();

        assertThrows(NullPointerException.class, () ->
                Shipping.of(cost, null, recipient, address)
        );
    }

    @Test
    void givenNullRecipient_whenCreate_thenShouldThrowException() {
        final var cost = Money.of(MONEY_20);
        final var expectedDate = LocalDate.now();
        final var address = aAddress().build();

        assertThrows(NullPointerException.class, () ->
                Shipping.of(cost, expectedDate, null, address)
        );
    }

    @Test
    void givenNullAddress_whenCreate_thenShouldThrowException() {
        final var cost = Money.of(MONEY_20);
        final var expectedDate = LocalDate.now();
        final var recipient = aRecipient();

        assertThrows(NullPointerException.class, () ->
                new Shipping(cost, expectedDate, recipient, null)
        );
    }

}
