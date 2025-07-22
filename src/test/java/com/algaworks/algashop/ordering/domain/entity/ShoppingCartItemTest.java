package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.entity.fixture.ProductTestFixture.aProduct;
import static com.algaworks.algashop.ordering.domain.entity.fixture.ShoppingCartItemTestFixture.aShoppingCartItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class ShoppingCartItemTest {


    @Test
    void givenValidData_whenCreateNewItem_shouldInitializeCorrectly() {
        var item = aShoppingCartItem()
                .productName(ProductName.of("Notebook"))
                .price(Money.of("2000"))
                .quantity(Quantity.of(2))
                .available(true)
                .build();

        assertWith(item,
                i -> assertThat(i.id()).isNotNull(),
                i -> assertThat(i.shoppingCartId()).isNotNull(),
                i -> assertThat(i.productId()).isNotNull(),
                i -> assertThat(i.productName()).isEqualTo(ProductName.of("Notebook")),
                i -> assertThat(i.price()).isEqualTo(Money.of("2000")),
                i -> assertThat(i.quantity()).isEqualTo(Quantity.of(2)),
                i -> assertThat(i.isAvailable()).isTrue(),
                i -> assertThat(i.totalAmount()).isEqualTo(Money.of("4000"))
        );
    }

    @Test
    void givenItem_whenChangeQuantity_shouldRecalculateTotal() {
        ShoppingCartItem item = aShoppingCartItem()
                .price(Money.of("1000"))
                .quantity(Quantity.of(1))
                .build();

        item.changeQuantity(Quantity.of(3));

        assertWith(item,
                i -> assertThat(i.quantity()).isEqualTo(Quantity.of(3)),
                i -> assertThat(i.totalAmount()).isEqualTo(Money.of("3000"))
        );
    }

    @Test
    void givenItem_whenChangePrice_shouldRecalculateTotal() {
        ShoppingCartItem item = aShoppingCartItem()
                .price(Money.of("1500"))
                .quantity(new Quantity(2))
                .build();

        var product = aProduct().build();
        item.refresh(product);

        assertWith(item,
                i -> assertThat(i.price()).isEqualTo(product.price()),
                i -> assertThat(i.totalAmount()).isEqualTo(product.price().multiply(Quantity.of(2)))
        );
    }

    @Test
    void givenItem_whenChangeAvailability_shouldUpdateStatus() {
        ShoppingCartItem item = aShoppingCartItem()
                .available(true)
                .build();

        Product product = aProduct()
                .inStock(false)
                .build();

        item.refresh(product);

        assertThat(item.isAvailable()).isFalse();
    }

    @Test
    void givenEqualIds_whenCompareItems_shouldBeEqual() {
        var cartId = ShoppingCartId.of();
        var productId = ProductId.of();
        var shoppingCartItemId = ShoppingCartItemId.of();

        ShoppingCartItem item1 = ShoppingCartItem.existing()
                .id(shoppingCartItemId)
                .shoppingCartId(cartId)
                .productId(productId)
                .productName(ProductName.of("Mouse"))
                .price(Money.of("100"))
                .quantity(Quantity.of(1))
                .available(true)
                .totalAmount(Money.of("100"))
                .build();

        ShoppingCartItem item2 = ShoppingCartItem.existing()
                .id(shoppingCartItemId)
                .shoppingCartId(cartId)
                .productId(productId)
                .productName(ProductName.of("Notebook"))
                .price(Money.of("100"))
                .quantity(Quantity.of(1))
                .available(true)
                .totalAmount(Money.of("100"))
                .build();

        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).hasSameHashCodeAs(item2.hashCode());
    }

    @Test
    void givenDifferentIds_whenCompareItems_shouldNotBeEqual() {
        var item1 = aShoppingCartItem().build();
        var item2 = aShoppingCartItem().build();
        assertThat(item1).isNotEqualTo(item2);
    }

}
