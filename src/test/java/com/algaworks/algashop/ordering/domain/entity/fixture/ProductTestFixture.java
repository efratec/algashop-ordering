package com.algaworks.algashop.ordering.domain.entity.fixture;

import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;

public class ProductTestFixture {

    public static final ProductId DEFAULT_PRODUCT_ID = ProductId.of();

    private ProductTestFixture() {
    }

    public static Product.ProductBuilder aProduct() {
        return Product.builder()
                .id(DEFAULT_PRODUCT_ID)
                .inStock(true)
                .name(ProductName.of("Notebook X11"))
                .price(Money.of("3000"));
    }

    public static Product.ProductBuilder aProductUnavailable() {
        return Product.builder()
                .id(DEFAULT_PRODUCT_ID)
                .name(ProductName.of("Desktop FX9000"))
                .price(Money.of("5000"))
                .inStock(false);
    }

    public static Product.ProductBuilder aProductAltRamMemory() {
        return Product.builder()
                .id(DEFAULT_PRODUCT_ID)
                .name(ProductName.of("4GB RAM"))
                .price(Money.of("200"))
                .inStock(true);
    }

    public static Product.ProductBuilder aProductAltMousePad() {
        return Product.builder()
                .id(DEFAULT_PRODUCT_ID)
                .name(ProductName.of("Mouse Pad"))
                .price(Money.of("100"))
                .inStock(true);
    }

}
