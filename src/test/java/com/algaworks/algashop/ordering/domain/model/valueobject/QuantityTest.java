package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {

    @Test
    void givenValidValue_whenCreate_thenShouldCreateQuantity() {
        Quantity quantity = Quantity.of(5);
        assertEquals(5, quantity.value());
    }

    @Test
    void givenZeroValue_whenCreate_thenShouldCreateQuantity() {
        Quantity quantity = Quantity.of(0);
        assertEquals(0, quantity.value());
    }

    @Test
    void givenNegativeValue_whenCreate_thenShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> Quantity.of(-1));
    }

    @Test
    void givenNullValue_whenCreate_thenShouldThrowException() {
        assertThrows(NullPointerException.class, () -> Quantity.of(null));
    }

    @Test
    void givenTwoQuantities_whenAdd_thenShouldReturnSum() {
        final var quantity3 = Quantity.of(3);
        final var quantity2 = Quantity.of(2);
        final var result = quantity3.add(quantity2);
        assertEquals(5, result.value());
    }

    @Test
    void givenNull_whenAdd_thenShouldThrowException() {
        final var q1 = Quantity.of(1);
        assertThrows(NullPointerException.class, () -> q1.add(null));
    }

    @Test
    void testCompareTo() {
        final var quantity2 = Quantity.of(2);
        final var quantity5 = Quantity.of(5);
        assertTrue(quantity2.compareTo(quantity5) < 0);
        assertEquals(0, quantity2.compareTo(Quantity.of(2)));
        assertTrue(quantity5.compareTo(quantity2) > 0);
    }

    @Test
    void testToString() {
        final var quantity = Quantity.of(10);
        assertEquals("10", quantity.toString());
    }

    @Test
    void givenStaticZero_thenShouldReturnZeroInstance() {
        assertEquals(0, Quantity.ZERO.value());
    }

}
