package com.algaworks.algashop.ordering.presentation.shoppingcart;

import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import com.algaworks.algashop.ordering.application.shoppingcart.query.*;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.validator.FieldValidations;
import com.algaworks.algashop.ordering.presentation.UnprocessableEntityException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartQueryService shoppingCartQueryService;
    private final ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartOutput create(@RequestBody @Valid ShoppingCartInput input) {
        UUID shoppingCartId = null;
        try {
            shoppingCartId = shoppingCartManagementApplicationService.createNew(input.getCustomerId());
        } catch (CustomerNotFoundException e) {
            UnprocessableEntityException.of(e.getMessage(), e);
        }
        return shoppingCartQueryService.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}")
    public ShoppingCartOutput findById(@PathVariable UUID shoppingCartId) {
        return shoppingCartQueryService.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}/items")
    public ShoppingCartItemListModel findItemsById(@PathVariable UUID shoppingCartId) {
        var items = shoppingCartQueryService.findById(shoppingCartId).getItems();
        return ShoppingCartItemListModel.builder().items(items).build();
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID shoppingCartId) {
        shoppingCartManagementApplicationService.delete(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void empty(@PathVariable UUID shoppingCartId) {
        shoppingCartManagementApplicationService.empty(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@PathVariable UUID shoppingCartId,
                        @RequestBody @Valid ShoppingCartItemInput input) {
        input.setShoppingCartId(shoppingCartId);
        try {
            shoppingCartManagementApplicationService.addItem(input);
        } catch (ProductNotFoundException e) {
            UnprocessableEntityException.of(e.getMessage(), e);
        }

    }

    @DeleteMapping("/{shoppingCartId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable UUID shoppingCartId,
                           @PathVariable UUID itemId) {
        shoppingCartManagementApplicationService.removeItem(shoppingCartId, itemId);
    }

}
