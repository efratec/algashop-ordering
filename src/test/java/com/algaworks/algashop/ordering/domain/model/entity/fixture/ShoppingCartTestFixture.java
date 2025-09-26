package com.algaworks.algashop.ordering.domain.model.entity.fixture;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;

public class ShoppingCartTestFixture {

    public CustomerId customerId = DEFAULT_CUSTOMER_ID;
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
