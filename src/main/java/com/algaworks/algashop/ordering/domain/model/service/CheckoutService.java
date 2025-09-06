package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;

import java.util.Set;

import static com.algaworks.algashop.ordering.domain.model.exception.enums.OrderReason.NO_SHOPPING_CART_IS_NOT_AVAILABLE;
import static com.algaworks.algashop.ordering.domain.model.validator.FieldValidations.validate;

@DomainService
public class CheckoutService {

    public Order checkout(ShoppingCart shoppingCart,
                          Billing billing, Shipping shipping,
                          PaymentMethodEnum paymentMethod) {

        validate(() -> (shoppingCart.isContainsUnavailableItems() || shoppingCart.isEmpty()),
                NO_SHOPPING_CART_IS_NOT_AVAILABLE, ShoppingCartCantProceedToCheckoutException::new,
                shoppingCart.id());

        Set<ShoppingCartItem> items = shoppingCart.items();

        var orderStarted = startOrder(shoppingCart, billing, shipping, paymentMethod);
        addOrderItem(items, orderStarted);

        orderStarted.place();
        shoppingCart.empty();

        return orderStarted;
    }

    private void addOrderItem(Set<ShoppingCartItem> items, Order orderStarted) {
        items.forEach(item -> {
            var product = Product.of(item.productId(), item.productName(), item.price(), item.isAvailable());
            orderStarted.addItem(product, item.quantity());
        });
    }

    private Order startOrder(ShoppingCart shoppingCart,
                             Billing billing,
                             Shipping shipping,
                             PaymentMethodEnum paymentMethod) {

        var order = Order.draft(shoppingCart.customerId());
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        return order;
    }

}
