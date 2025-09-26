package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.NO_CUSTOMER_NOT_FOUND;

public class CustomerNotFoundException extends DomainException {

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public static CustomerNotFoundException because(UUID id) {
      return of(CustomerNotFoundException::new, NO_CUSTOMER_NOT_FOUND, id);
    }

}

