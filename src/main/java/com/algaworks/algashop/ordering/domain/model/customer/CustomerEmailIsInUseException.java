package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.CUSTOMER_EMAIL_IS_ALREADY_USED;

public class CustomerEmailIsInUseException extends DomainException {

    public CustomerEmailIsInUseException(String message) {
        super(message);
    }

    public static CustomerEmailIsInUseException because(UUID id) {
        return of(CustomerEmailIsInUseException::new, CUSTOMER_EMAIL_IS_ALREADY_USED, id);
    }


}
