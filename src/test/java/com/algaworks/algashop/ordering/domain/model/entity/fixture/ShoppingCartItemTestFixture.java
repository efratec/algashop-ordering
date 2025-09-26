package com.algaworks.algashop.ordering.domain.model.entity.fixture;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;

public class ShoppingCartItemTestFixture {

    private ShoppingCartId shoppingCartId = ShoppingCartTestFixture.DEFAULT_SHOPPING_CART_ID;
    private ProductId productId = ProductTestFixture.DEFAULT_PRODUCT_ID;
    private ProductName productName = ProductName.of("Notebook");
    private Money price = Money.of("1000");
    private Quantity quantity = Quantity.of(1);
    private boolean available = true;

    private ShoppingCartItemTestFixture() {
    }

    public static ShoppingCartItemTestFixture aShoppingCartItem() {
        return new ShoppingCartItemTestFixture();
    }

    public ShoppingCartItem build() {
        return ShoppingCartItem.brandNew()
                .shoppingCartId(shoppingCartId)
                .productId(productId)
                .productName(productName)
                .price(price)
                .quantity(quantity)
                .available(available)
                .build();
    }

    public ShoppingCartItemTestFixture shoppingCartId(ShoppingCartId shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
        return this;
    }

    public ShoppingCartItemTestFixture productId(ProductId productId) {
        this.productId = productId;
        return this;
    }

    public ShoppingCartItemTestFixture productName(ProductName productName) {
        this.productName = productName;
        return this;
    }

    public ShoppingCartItemTestFixture price(Money price) {
        this.price = price;
        return this;
    }

    public ShoppingCartItemTestFixture quantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public ShoppingCartItemTestFixture available(boolean available) {
        this.available = available;
        return this;
    }

}
