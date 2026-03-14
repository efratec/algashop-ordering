package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.Specification;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customer;

import java.time.Year;

public record CustomerHasOrderedEnoughAtYearSpecification(Orders orders,
                                                          long expectedOrderCount)
        implements Specification<Customer> {

    @Override
    public boolean isSatisfiedBy(Customer customer) {
        return orders.salesQuantityByCustomerInYear(
                customer.id(),
                Year.now()
        ) >= expectedOrderCount;
    }
}
