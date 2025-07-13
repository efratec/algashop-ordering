package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PhoneTest {

    @Test
    void given_phoneIsNull_whenCreate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(()-> new Phone(null));
    }

    @Test
    void given_phoneIsBlak_whenCreate_shouldGenerateException() {
        var phoneBlank = "";
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> new Phone(phoneBlank));
    }

    @Test
    void given_phoneValid_whenCreate_shouldSetValue() {
        var phoneBlank = "82 9889900";
        Assertions.assertThat(phoneBlank).isEqualTo(new Phone(phoneBlank).value());
    }

}
