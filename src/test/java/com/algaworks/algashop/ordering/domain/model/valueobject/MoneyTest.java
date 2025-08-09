package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTest {

    @Test
    void givenValidValue_whenCreate_thenShouldCreateQuantity() {
        final var valueInput = BigDecimal.valueOf(5).setScale(2,RoundingMode.HALF_EVEN);
        assertEquals(valueInput, Money.of("5").value());
    }

    @Test
    void givenZeroValue_whenCreate_thenShouldCreateQuantity() {
        final var valueInput = BigDecimal.ZERO.setScale(2,RoundingMode.HALF_EVEN);
        assertEquals(valueInput, Money.of("0").value());
    }

    @Test
    void givenNegativeValue_whenCreate_thenShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> Money.of("-1"));
    }

    @Test
    void givenTwoMoneyValues_whenAdd_thenShouldReturnSum() {
        final var value1 = Money.of(BigDecimal.valueOf(5));
        final var value2 = Money.of(BigDecimal.valueOf(5));
        final var result = value1.add(value2);
        assertEquals(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_EVEN), result.value());
    }

    @Test
    void givenNull_whenAdd_thenShouldThrowException() {
        final var money = Money.of(BigDecimal.valueOf(5));
        assertThrows(NullPointerException.class, () -> money.add(null));
    }

    @Test
    void givenNull_whenMultiply_thenShouldThrowException() {
        final var money = Money.of(BigDecimal.valueOf(5));
        assertThrows(NullPointerException.class, () -> money.multiply(null));
    }

    @Test
    void givenMoney_when_MultiplyByQuantity_thenShouldReturnMultiplied() {
        final var value1 = Money.of(BigDecimal.valueOf(5));
        final var result = value1.multiply(Quantity.of(10));
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_EVEN), result.value());
    }

    @Test
    void testCompareTo() {
        final var value2 = BigDecimal.valueOf(2);
        final var value5 = BigDecimal.valueOf(5);
        final var money2 = Money.of(value2);
        final var money5 = Money.of(value5);

        assertTrue(money2.compareTo(money5) < 0);
        assertEquals(0, money2.compareTo(Money.of(value2)));
        assertTrue(money5.compareTo(money2) > 0);
    }

    @Test
    void testToString() {
        final var quantity = Money.of(BigDecimal.valueOf(10));
        assertEquals("10.00", quantity.toString());
    }

}
