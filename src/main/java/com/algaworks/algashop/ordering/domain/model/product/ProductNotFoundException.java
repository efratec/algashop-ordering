package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.NO_PRODUCT_NOT_FOUND;

public class ProductNotFoundException extends DomainException {

    protected ProductNotFoundException(String message) {
        super(message);
    }

    public static ProductNotFoundException because(UUID id) {
        return of(ProductNotFoundException::new, NO_PRODUCT_NOT_FOUND, id);
    }

}
