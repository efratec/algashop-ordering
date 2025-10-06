package com.algaworks.algashop.ordering.application.customer.loyaltypoints;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerLoyaltPointsService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerLoyaltyPointsApplicationService {

    private final Orders orders;
    private final Customers customers;

    private final CustomerLoyaltPointsService customerLoyaltPointsService;

    @Transactional
    public void addLoyaltyPoints(UUID rawCustomerId, String rawOrderId) {
        Objects.requireNonNull(rawCustomerId);
        Objects.requireNonNull(rawOrderId);

        var customer = customers.ofId(CustomerId.from(rawCustomerId))
                .orElseThrow(() -> CustomerNotFoundException.because(rawCustomerId));

        var order = orders.ofId(OrderId.from(rawOrderId))
                .orElseThrow(() -> OrderNotFoundException.because(TSID.from(rawOrderId)));

        customerLoyaltPointsService.addPoints(customer, order);

        customers.add(customer);
    }

}
