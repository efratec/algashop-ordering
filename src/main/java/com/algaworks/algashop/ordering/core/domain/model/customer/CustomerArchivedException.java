package com.algaworks.algashop.ordering.core.domain.model.customer;

import com.algaworks.algashop.ordering.core.domain.exception.DomainException;

import static com.algaworks.algashop.ordering.core.domain.exception.enums.ReasonMessageEnum.CUSTOMER_IS_ARCHIVED;

public class CustomerArchivedException extends DomainException {

    public CustomerArchivedException(String message) {
        super(message);
    }

    public static CustomerArchivedException becauseCustomerIsArchived() {
        return of(CustomerArchivedException::new, CUSTOMER_IS_ARCHIVED);
    }

}
