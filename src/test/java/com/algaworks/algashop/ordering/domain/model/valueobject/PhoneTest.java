package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PhoneTest {

    @Test
    void given_phoneIsNull_whenCreate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(()-> Phone.of(null));
    }

    @Test
    void given_phoneIsBlank_whenCreate_shouldGenerateException() {
        var phoneBlank = "";
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> Phone.of(phoneBlank));
    }

    @Test
    void given_phoneValid_whenCreate_shouldSetValue() {
        var phoneBlank = "82 9889900";
        Assertions.assertThat(phoneBlank).isEqualTo(Phone.of(phoneBlank).value());
    }

}
