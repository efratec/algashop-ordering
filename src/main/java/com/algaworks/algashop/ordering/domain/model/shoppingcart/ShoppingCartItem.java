package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.INVALID_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT;
import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.requireAllNonNull;
import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.validate;

@EqualsAndHashCode(of = "id")
public class ShoppingCartItem {

    private ShoppingCartItemId id;
    private ShoppingCartId shoppingCartId;
    private ProductId productId;
    private ProductName productName;
    private Money price;
    private Quantity quantity;
    private Money totalAmount;

    @Getter
    private boolean available;

    @Builder(builderClassName = "ExistingShoppingCartItem", builderMethodName = "existing")
    public ShoppingCartItem(ShoppingCartItemId id, ShoppingCartId shoppingCartId, ProductId productId, ProductName productName,
                            Money price, Quantity quantity, Boolean available, Money totalAmount) {
        this.setId(id);
        this.setShoppingCartId(shoppingCartId);
        this.setProductId(productId);
        this.setProductName(productName);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setAvailable(available);
        this.setTotalAmount(totalAmount);
    }

    @Builder(builderClassName = "BrandNewShoppingCartItem", builderMethodName = "brandNew")
    public ShoppingCartItem(ShoppingCartId shoppingCartId,
                            ProductId productId, ProductName productName, Money price,
                            Quantity quantity, Boolean available) {
        this(ShoppingCartItemId.of(), shoppingCartId, productId, productName, price, quantity, available, Money.ZERO());
        this.recalculateTotals();
    }

    public void refresh(Product newProduct) {
        requireAllNonNull("Product", newProduct, "Product.Id()", newProduct.id());

        validate(() -> !this.productId().equals(newProduct.id()), INVALID_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT,
                ShoppingCartItemIncompatibleProductException::new, this.id(), this.productId());

        this.setPrice(newProduct.price());
        this.setAvailable(newProduct.inStock());
        this.setProductName(newProduct.name());
        this.recalculateTotals();
    }

    public void changeQuantity(Quantity newQuantity) {
        this.setQuantity(newQuantity);
        this.recalculateTotals();
    }

    public ShoppingCartItemId id() {
        return id;
    }

    public ShoppingCartId shoppingCartId() {
        return shoppingCartId;
    }

    public ProductId productId() {
        return productId;
    }

    public ProductName productName() {
        return productName;
    }

    public Money price() {
        return price;
    }

    public Quantity quantity() {
        return quantity;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    private void recalculateTotals() {
        this.setTotalAmount(price.multiply(quantity));
    }

    private void setId(ShoppingCartItemId id) {
        this.id = id;
    }

    private void setShoppingCartId(ShoppingCartId shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    private void setProductId(ProductId productId) {
        this.productId = productId;
    }

    private void setProductName(ProductName productName) {
        this.productName = productName;
    }

    private void setPrice(Money price) {
        this.price = price;
    }

    private void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    private void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    private void setAvailable(boolean available) {
        this.available = available;
    }

}
