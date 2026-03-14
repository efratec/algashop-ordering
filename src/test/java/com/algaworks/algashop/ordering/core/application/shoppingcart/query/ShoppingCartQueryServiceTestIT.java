package com.algaworks.algashop.ordering.core.application.shoppingcart.query;

import com.algaworks.algashop.ordering.core.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.entity.fixture.CustomerTestFixture;
import com.algaworks.algashop.ordering.core.domain.model.entity.fixture.ShoppingCartTestFixture;
import com.algaworks.algashop.ordering.core.domain.model.order.Orders;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShoppingCartQueryServiceTestIT extends AbstractApplicationIT {

    private final ShoppingCartQueryService queryService;
    private final ShoppingCarts shoppingCarts;
    private final Orders orders;
    private final Customers customers;

    @Test
    void shouldFindById() {
        var customer = CustomerTestFixture.existingCustomer().build();
        customers.add(customer);

        var shoppingCart = ShoppingCartTestFixture.aShoppingCart().customerId(customer.id()).build();
        shoppingCarts.add(shoppingCart);

        ShoppingCartOutput output = queryService.findById(shoppingCart.id().value());
        assertThat(output)
                .extracting(ShoppingCartOutput::getId,
                        ShoppingCartOutput::getCustomerId,
                        ShoppingCartOutput::getTotalAmount,
                        ShoppingCartOutput::getTotalItems
                ).containsExactly(
                        shoppingCart.id().value(),
                        shoppingCart.customerId().value(),
                        shoppingCart.totalAmount().value(),
                        shoppingCart.totalItems().value()
                );

        assertThat(output.getItems()).hasSize(2);
    }

    @Test
    void givenShoppingCartInvalid_shouldThrowException() {
      assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> queryService.findById(ShoppingCartId.of().value()));
    }

    @Test
    void shouldFindByCustomerId() {
        var customer = CustomerTestFixture.existingCustomer().build();
        customers.add(customer);

        var shoppingCart = ShoppingCartTestFixture.aShoppingCart().customerId(customer.id()).build();
        shoppingCarts.add(shoppingCart);

        ShoppingCartOutput output = queryService.findByCustomerId(customer.id().value());
        assertThat(output)
                .extracting(ShoppingCartOutput::getId,
                        ShoppingCartOutput::getCustomerId,
                        ShoppingCartOutput::getTotalAmount,
                        ShoppingCartOutput::getTotalItems
                ).containsExactly(
                        shoppingCart.id().value(),
                        shoppingCart.customerId().value(),
                        shoppingCart.totalAmount().value(),
                        shoppingCart.totalItems().value()
                );

        assertThat(output.getItems()).hasSize(2);
    }

    @Test
    void givenShoppingCartWithCustomerInvalid_shouldThrowException() {
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> queryService.findByCustomerId(CustomerId.of().value()));
    }

}