package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler.convertBillingToEmbeddable;
import static org.assertj.core.api.Assertions.assertThat;

class OrderPersistenceEntityAssemblerTest {

    private final OrderPersistenceEntityAssembler assembler = new OrderPersistenceEntityAssembler();

    @Test
    void shouldConvertToDomain() {
        var order = OrderTestFixture.anOrder().build();
        var orderPersistenceEntity = assembler.fromDomain(order);
        assertThat(orderPersistenceEntity).satisfies(
                p-> assertThat(p.getId()).isEqualTo(order.id().value().toLong()),
                p-> assertThat(p.getCustomerId()).isEqualTo(order.customerId().value()),
                p -> assertThat(p.getTotalAmount()).isEqualTo(order.totalAmount().value()),
                p -> assertThat(p.getTotalItems()).isEqualTo(order.totalItems().value()),
                p -> assertThat(p.getStatus()).isEqualTo(order.status().name()),
                p -> assertThat(p.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
                p -> assertThat(p.getPlacedAt()).isEqualTo(order.placedAt()),
                p -> assertThat(p.getPaidAt()).isEqualTo(order.paidAt()),
                p -> assertThat(p.getCanceledAt()).isEqualTo(order.canceledAt()),
                p -> assertThat(p.getReadyAt()).isEqualTo(order.readyAt()),
                p -> assertThat(p.getBilling()).isEqualTo(convertBillingToEmbeddable(order.billing())),
                p -> assertThat(p.getShipping()).isEqualTo(OrderPersistenceEntityAssembler.convertShippingToEmbeddable(order.shipping()))
        );
    }
    
}