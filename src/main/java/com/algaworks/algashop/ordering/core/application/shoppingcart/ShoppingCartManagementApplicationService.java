package com.algaworks.algashop.ordering.core.application.shoppingcart;

import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemInput;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.*;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ForManagingShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartManagementApplicationService implements ForManagingShoppingCarts {

    private final ShoppingCarts shoppingCarts;
    private final ProductCatalogService productCatalogService;
    private final ShoppingCartService shoppingService;

    @Transactional
    @Override
    public void addItem(ShoppingCartItemInput itemInput) {
        Objects.requireNonNull(itemInput);

        var shoppingCartId = ShoppingCartId.from(itemInput.getShoppingCartId());
        var productId = ProductId.from(itemInput.getProductId());

        var shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> ShoppingCartNotFoundException.because(shoppingCartId.value()));

        var product = productCatalogService.ofId(productId)
                .orElseThrow(() -> ProductNotFoundException.because(productId.value()));

        shoppingCart.addItem(product, Quantity.of(itemInput.getQuantity()));

        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    @Override
    public UUID createNew(UUID rawCustomerId) {
        Objects.requireNonNull(rawCustomerId);
        var shoppingCart = shoppingService.startShopping(CustomerId.from((rawCustomerId)));
        shoppingCarts.add(shoppingCart);
        return shoppingCart.id().value();
    }

    @Transactional
    @Override
    public void removeItem(UUID rawShoppingCartId, UUID shoppingCartItemId) {
        Objects.requireNonNull(rawShoppingCartId);
        Objects.requireNonNull(shoppingCartItemId);

        var shoppingCartId = ShoppingCartId.from(rawShoppingCartId);
        var shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> ShoppingCartNotFoundException.because(shoppingCartId.value()));

        shoppingCart.removeItem(ShoppingCartItemId.from(shoppingCartItemId));
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    @Override
    public void empty(UUID rawShoppingCartId) {
        Objects.requireNonNull(rawShoppingCartId);

        var shoppingCartId = new ShoppingCartId(rawShoppingCartId);
        var shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> ShoppingCartNotFoundException.because(shoppingCartId.value()));

        shoppingCart.empty();
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    @Override
    public void delete(UUID rawShoppingCartId) {
        Objects.requireNonNull(rawShoppingCartId);

        var shoppingCartId = new ShoppingCartId(rawShoppingCartId);
        var shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> ShoppingCartNotFoundException.because(shoppingCartId.value()));

        shoppingCarts.remove(shoppingCart);
    }

}
