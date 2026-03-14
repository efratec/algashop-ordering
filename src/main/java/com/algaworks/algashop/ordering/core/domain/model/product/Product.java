package com.algaworks.algashop.ordering.core.domain.model.product;

import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import lombok.Builder;

import static com.algaworks.algashop.ordering.core.domain.exception.enums.ReasonMessageEnum.NO_PRODUCT_IS_OUT_OF_STOCK;
import static com.algaworks.algashop.ordering.core.domain.validator.FieldValidations.requireAllNonNull;
import static com.algaworks.algashop.ordering.core.domain.validator.FieldValidations.validate;

@Builder
public record Product(ProductId id,
                      ProductName name,
                      Money price,
                      Boolean inStock) {

    public Product {
        requireAllNonNull("Id", id, "Name", name, "Price", price, "Stock", inStock);
    }

    public static Product of(ProductId id, ProductName name, Money price, Boolean isStock) {
        return new Product(id, name, price, isStock);
    }

    public void checkoutOfStock() {
        validate(this::isOutOfStock, NO_PRODUCT_IS_OUT_OF_STOCK, ProductOutOfStockException::new, this.id());
    }

    private boolean isOutOfStock() {
        return !inStock();
    }


}
