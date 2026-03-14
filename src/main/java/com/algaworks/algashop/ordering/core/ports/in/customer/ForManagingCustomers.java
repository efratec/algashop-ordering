package com.algaworks.algashop.ordering.core.ports.in.customer;

import java.util.UUID;

public interface ForManagingCustomers {

    UUID create(CustomerInput input);
    void update(UUID rawCustomerId, CustomerUpdateInput input);
    void archive(UUID customerId);
    void changeEmail(UUID customerId, String newEmail);

}
