package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import lombok.Builder;

import static com.algaworks.algashop.ordering.domain.model.exception.enums.OrderReason.NO_PRODUCT_IS_OUT_OF_STOCK;
import static com.algaworks.algashop.ordering.domain.model.validator.FieldValidations.requireAllNonNull;
import static com.algaworks.algashop.ordering.domain.model.validator.FieldValidations.validate;

@Builder
public record Product(ProductId id,
                      ProductName name,
                      Money price,
                      Boolean inStock) {

    public Product {
        requireAllNonNull("Id", id, "Name", name, "Price", price, "InStock", inStock);
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
