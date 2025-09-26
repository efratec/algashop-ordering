package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.product.Product;

import java.util.Set;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.NO_SHOPPING_CART_IS_NOT_AVAILABLE;
import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.validate;

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
