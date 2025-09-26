package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import lombok.RequiredArgsConstructor;

import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.CUSTOMER_ALREADY_HAVE_SHOPPING_CART_EXIST;
import static com.algaworks.algashop.ordering.domain.exception.enums.ReasonMessageEnum.NO_CUSTOMER_NOT_FOUND;
import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.validate;

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
