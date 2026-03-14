package com.algaworks.algashop.ordering.core.domain.model.product;

import com.algaworks.algashop.ordering.core.domain.exception.DomainException;

public class ProductOutOfStockException extends DomainException {

    public ProductOutOfStockException(String message) {
        super(message);
    }

}
