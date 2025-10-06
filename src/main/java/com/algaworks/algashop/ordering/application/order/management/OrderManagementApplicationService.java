package com.algaworks.algashop.ordering.application.order.management;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderManagementApplicationService {

    private final Orders orders;

    @Transactional
    public void cancel(Long rawOrderId) {
        var order = getOrder((rawOrderId));
        order.cancel();
        orders.add(order);
    }

    @Transactional
    public void markAsPaid(Long rawOrderId) {
        var order = getOrder((rawOrderId));
        order.markAsPaid();
        orders.add(order);
    }

    @Transactional
    public void markAsReady(Long rawOrderId) {
        var order = getOrder((rawOrderId));
        order.markAsReady();
        orders.add(order);
    }

    private Order getOrder(Long rawOrderId) {
        return orders.ofId(OrderId.from(rawOrderId))
                .orElseThrow(() -> OrderNotFoundException.because(TSID.from(rawOrderId)));
    }

}
