package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class EmailTest {

    @Test
    void shouldSetEmailValue() {
        var emailExpected = UUID.randomUUID() + "@gmail.com";
        var email = new Email(emailExpected);
        Assertions.assertThat(email.value()).isEqualTo(emailExpected);
    }

    @Test
    void given_invalidEmail_whenCreate_shouldGenerateException() {
        var emailInvalid = "Invalid";
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> new Email(emailInvalid));
    }

    @Test
    void given_emailIsNull_whenCreate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(()-> new Email(null));
    }

}
