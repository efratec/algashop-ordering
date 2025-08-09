package com.algaworks.algashop.ordering.domain.model.exception;

public class ProductOutOfStockException extends DomainException {

    public ProductOutOfStockException(String message) {
        super(message);
    }

}
