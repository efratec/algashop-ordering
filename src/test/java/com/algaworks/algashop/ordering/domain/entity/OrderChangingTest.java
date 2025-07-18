package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.entity.fixture.OrderTextFixture;
import com.algaworks.algashop.ordering.domain.entity.fixture.ProductTestFixture;
import com.algaworks.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderChangingTest {

    @ParameterizedTest
    @EnumSource(OrderStatusEnum.class)
    void givenOrderStatus_whenChangingFields_thenValidateEditPermission(OrderStatusEnum status) {
        var order = OrderTextFixture.anOrder().status(status).build();

        var product = ProductTestFixture.aProductAltMousePad().build();
        var quantity = Quantity.of(2);
        var billing = OrderTextFixture.aBilling();
        var shipping = OrderTextFixture.aShipping();
        var paymentMethod = PaymentMethod.CREDIT_CARD;

        var orderItem = order.items().iterator().next();

        List<Executable> operations = List.of(
                () -> order.addItem(product, quantity),
                () -> order.changeBilling(billing),
                () -> order.changeShipping(shipping),
                () -> order.changeItemQuantity(orderItem.id(), quantity),
                () -> order.changePaymentMethod(paymentMethod)
        );

        for (Executable operation : operations) {
            if (OrderStatusEnum.DRAFT.equals(status)) {
                assertThatCode(operation::execute).doesNotThrowAnyException();
            } else {
                assertThatThrownBy(operation::execute).isInstanceOf(OrderCannotBeEditedException.class);
            }
        }
    }

    @FunctionalInterface
    private interface Executable {
        void execute();
    }

}
