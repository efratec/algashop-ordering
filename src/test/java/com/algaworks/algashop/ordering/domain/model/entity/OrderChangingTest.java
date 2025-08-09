package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture;
import com.algaworks.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderChangingTest {

    @ParameterizedTest
    @EnumSource(OrderStatusEnum.class)
    void givenOrderStatus_whenChangingFields_thenValidateEditPermission(OrderStatusEnum status) {
        var order = OrderTestFixture.anOrder().status(status).build();

        var product = ProductTestFixture.aProductAltMousePad().build();
        var quantity = Quantity.of(2);
        var billing = OrderTestFixture.aBilling();
        var shipping = OrderTestFixture.aShipping();
        var paymentMethod = PaymentMethodEnum.CREDIT_CARD;

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
