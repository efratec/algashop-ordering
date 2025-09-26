package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

public class ProductOutOfStockException extends DomainException {

    public ProductOutOfStockException(String message) {
        super(message);
    }

}
