package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoyaltyPointsTest {

    @Test
    void shouldGenerateWithValue() {
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);
        assertThat(loyaltyPoints.value()).isEqualTo(10);
    }

    @Test
    void shouldAddValue() {
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);
        assertThat(loyaltyPoints.add(5).value()).isEqualTo(15);
    }

    @Test
    void shouldNotAddValue() {
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> loyaltyPoints.add(-5));

        assertThat(loyaltyPoints.value()).isEqualTo(10);
    }

    @Test
    void shouldNotAddZeroValue() {
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> loyaltyPoints.add(0));

        assertThat(loyaltyPoints.value()).isEqualTo(10);
    }

}
