package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class BirthDateTest {

    @Test
    void given_birthDateIsNull_whenCreate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(()-> BirthDate.of(null));
    }

    @Test
    void given_birthDateIsAfterCurrentDate_whenCreate_shouldGenerateException() {
        var dateAfterCurrentDate = LocalDate.of(2026, 8, 1);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> BirthDate.of(dateAfterCurrentDate));
    }

    @Test
    void given_birthDateValid_whenCreate_shouldSetValue() {
        LocalDate inputDate = LocalDate.of(2025, 5, 1); // exemplo
        BirthDate birthDate = BirthDate.of(inputDate);
        Assertions.assertThat(inputDate).isEqualTo(birthDate.value());
    }

}
