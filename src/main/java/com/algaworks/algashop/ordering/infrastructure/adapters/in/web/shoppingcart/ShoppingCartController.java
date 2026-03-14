package com.algaworks.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ForManagingShoppingCarts;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemInput;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import com.algaworks.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.UnprocessableEntityException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ForQueryingShoppingCarts forQueryingShoppingCarts;
    private final ForManagingShoppingCarts forManagingShoppingCarts;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartOutput create(@RequestBody @Valid ShoppingCartInput input) {
        UUID shoppingCartId = null;
        try {
            shoppingCartId = forManagingShoppingCarts.createNew(input.getCustomerId());
        } catch (CustomerNotFoundException e) {
            UnprocessableEntityException.of(e.getMessage(), e);
        }
        return forQueryingShoppingCarts.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}")
    public ShoppingCartOutput findById(@PathVariable UUID shoppingCartId) {
        return forQueryingShoppingCarts.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}/items")
    public ShoppingCartItemListModel findItemsById(@PathVariable UUID shoppingCartId) {
        var items = forQueryingShoppingCarts.findById(shoppingCartId).getItems();
        return ShoppingCartItemListModel.builder().items(items).build();
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID shoppingCartId) {
        forManagingShoppingCarts.delete(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void empty(@PathVariable UUID shoppingCartId) {
        forManagingShoppingCarts.empty(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@PathVariable UUID shoppingCartId,
                        @RequestBody @Valid ShoppingCartItemInput input) {
        input.setShoppingCartId(shoppingCartId);
        try {
            forManagingShoppingCarts.addItem(input);
        } catch (ProductNotFoundException e) {
            UnprocessableEntityException.of(e.getMessage(), e);
        }

    }

    @DeleteMapping("/{shoppingCartId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable UUID shoppingCartId,
                           @PathVariable UUID itemId) {
        forManagingShoppingCarts.removeItem(shoppingCartId, itemId);
    }

}
