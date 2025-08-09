package com.algaworks.algashop.ordering.domain.model.entity.fixture;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

public class ShoppingCartTestFixture {

    public CustomerId customerId = CustomerId.of();
    public static final ShoppingCartId DEFAULT_SHOPPING_CART_ID = ShoppingCartId.of();
    private boolean withItems = true;

    private ShoppingCartTestFixture() {
    }

    public static ShoppingCartTestFixture aShoppingCart() {
        return new ShoppingCartTestFixture();
    }

    public ShoppingCart build() {
        var cart = ShoppingCart.startShopping(customerId);

        if (withItems) {
            cart.addItem(
                    ProductTestFixture.aProduct().build(),
                    Quantity.of(2)
            );
            cart.addItem(
                    ProductTestFixture.aProductAltRamMemory().build(),
                    new Quantity(1)
            );
        }

        return cart;
    }

    public ShoppingCartTestFixture customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public ShoppingCartTestFixture withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

}
