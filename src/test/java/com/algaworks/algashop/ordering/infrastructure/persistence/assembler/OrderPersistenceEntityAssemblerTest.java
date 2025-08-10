package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderPersistenceEntityTestFixture;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.stream.Collectors;

import static com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler.convertBillingToEmbeddable;
import static org.assertj.core.api.Assertions.assertThat;

class OrderPersistenceEntityAssemblerTest {

    private final OrderPersistenceEntityAssembler assembler = new OrderPersistenceEntityAssembler();

    @Test
    void shouldConvertToDomain() {
        var order = OrderTestFixture.anOrder().build();
        var orderPersistenceEntity = assembler.fromDomain(order);
        assertThat(orderPersistenceEntity).satisfies(
                p -> assertThat(p.getId()).isEqualTo(order.id().value().toLong()),
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

    @Test
    void givenOrderWithoutItems_shouldRemovePersistenceEntityItems() {
        var order = OrderTestFixture.anOrder().withItems(false).build();
        var orderPersistenceEntity = OrderPersistenceEntityTestFixture.existingOrder().build();

        assertThat(order.items()).isEmpty();
        assertThat(orderPersistenceEntity.getItems()).isNotEmpty();

        assembler.merge(orderPersistenceEntity, order);

        assertThat(orderPersistenceEntity.getItems()).isEmpty();
    }

    @Test
    void givenOrderWithItems_shouldAddPersistenceEntity() {
        var order = OrderTestFixture.anOrder().withItems(true).build();
        var orderPersistenceEntity = OrderPersistenceEntityTestFixture.existingOrder()
                .items(new HashSet<>()).build();

        assertThat(order.items()).isNotEmpty();
        assertThat(orderPersistenceEntity.getItems()).isEmpty();

        assembler.merge(orderPersistenceEntity, order);

        assertThat(orderPersistenceEntity.getItems()).isNotEmpty();
        assertThat(orderPersistenceEntity.getItems()).size().isEqualTo(order.items().size());
    }

    @Test
    void givenOrderWithItems_whenMerge_shouldRemoveMergeCorrectly() {
        var order = OrderTestFixture.anOrder().withItems(true).build();

        assertThat(order.items()).size().isEqualTo(2);

        var orderITemPersistenceEntities = order.items().stream()
                .map(assembler::fromDomain)
                .collect(Collectors.toSet());

        var orderPersistenceEntity = OrderPersistenceEntityTestFixture.existingOrder()
                .items(orderITemPersistenceEntities)
                .build();

        order.removeItem(order.items().iterator().next().id());

        assembler.merge(orderPersistenceEntity, order);

    }
    
}