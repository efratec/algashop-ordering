package com.algaworks.algashop.ordering.core.application.order;

import com.algaworks.algashop.ordering.core.domain.model.order.Order;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.order.Orders;
import com.algaworks.algashop.ordering.core.ports.in.order.ForManagingOrders;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderManagementApplicationService implements ForManagingOrders {

    private final Orders orders;

    @Transactional
    @Override
    public void cancel(Long rawOrderId) {
        var order = getOrder((rawOrderId));
        order.cancel();
        orders.add(order);
    }

    @Transactional
    @Override
    public void markAsPaid(Long rawOrderId) {
        var order = getOrder((rawOrderId));
        order.markAsPaid();
        orders.add(order);
    }

    @Transactional
    @Override
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
