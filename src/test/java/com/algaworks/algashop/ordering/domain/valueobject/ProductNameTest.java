package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ProductNameTest {

    @Test
    void given_productNameIsNull_whenCreate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(()-> ProductName.of(null));
    }

    @Test
    void given_productNameBlank_whenCreate_shouldGenerateException() {
        var productNameBlank = "";
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> ProductName.of(productNameBlank));
    }

    @Test
    void given_productNameValid_whenCreate_shouldSetValue() {
        var productName = "Boneca da Estrela";
        Assertions.assertThat(productName).isEqualTo(ProductName.of(productName).value());
    }

}
