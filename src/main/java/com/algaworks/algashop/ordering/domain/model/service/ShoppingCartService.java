package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import lombok.RequiredArgsConstructor;

import static com.algaworks.algashop.ordering.domain.model.exception.enums.OrderReason.CUSTOMER_ALREADY_HAVE_SHOPPING_CART_EXIST;
import static com.algaworks.algashop.ordering.domain.model.exception.enums.OrderReason.NO_CUSTOMER_NOT_FOUND;
import static com.algaworks.algashop.ordering.domain.model.validator.FieldValidations.validate;

@DomainService
@RequiredArgsConstructor
public class ShoppingCartService {

    private final Customers customers;
    private final ShoppingCarts shoppingCarts;

    public ShoppingCart startShopping(CustomerId customerId) {
        validatedCustomer(customerId);
        return ShoppingCart.startShopping(customerId);
    }

    private void validatedCustomer(CustomerId customerId) {
        validate(() -> !customers.exists(customerId), NO_CUSTOMER_NOT_FOUND,
                CustomerNotFoundException::new, customerId);

        validate(() -> shoppingCarts.ofCustomer(customerId).isPresent(), CUSTOMER_ALREADY_HAVE_SHOPPING_CART_EXIST,
                CustomerAlreadyHaveShoppingCartException::new, customerId);
    }

}
