package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.model.order.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProductAltMousePad;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProductAltRamMemory;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;

class OrderRemoveItemTest {

    @Test
    void given_an_order_in_draft_when_remove_order_item_then_item_should_be_removed() {
        final var order = Order.draft(CustomerId.of());

        order.addItem(aProductAltMousePad().build(), Quantity.of(2));

        var orderItem1 = order.items().iterator().next();

        order.addItem(aProductAltRamMemory().build(), Quantity.of(2));
        order.removeItem(orderItem1.id());

        assertWith(order,
                o -> Assertions.assertThat(o.totalAmount()).isEqualTo(Money.of("400")));
    }

    @Test
    void givenDraftOrder_whenTryToRemoveNonExistentItem_shouldThrowException() {
        final var order = OrderTestFixture.anOrder().build();
        assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.removeItem(OrderItemId.of()));
    }

    @Test
    void givenPlacedOrder_whenTry_ToRemove_shouldThrowException() {
        final var order = OrderTestFixture.anOrder().build();
        final var orderItem = order.items().iterator().next();

        order.place();

        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.removeItem(orderItem.id()));
    }

}
