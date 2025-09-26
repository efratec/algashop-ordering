package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ShoppingCartTestFixture.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        ShoppingCartPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class
})
class ShoppingCartsPersistenceProviderIT {

    private final ShoppingCartPersistenceProvider shoppingCartPersistenceProvider;
    private final CustomersPersistenceProvider customersPersistenceProvider;
    private final ShoppingCartPersistenceEntityRepository entityRepository;

    @BeforeEach
    void setup() {
        if (!customersPersistenceProvider.exists(DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(existingCustomer().build());
        }
    }

    @Test
    void shouldPersistAndFind() {
        var shoppingCart = aShoppingCart().build();
        assertThat(shoppingCart.version()).isNull();

        shoppingCartPersistenceProvider.add(shoppingCart);
        assertThat(shoppingCart.version()).isNotNull();

        var shoppingCartFound = shoppingCartPersistenceProvider.ofId(shoppingCart.id()).orElseThrow();
        assertThat(shoppingCartFound).isNotNull();
        assertThat(shoppingCartFound.id()).isEqualTo(shoppingCart.id());
    }

    @Test
    void shouldFindShoppingCartByCustomerId() {
        var shoppingCart = aShoppingCart().customerId(DEFAULT_CUSTOMER_ID)
                .build();
        shoppingCartPersistenceProvider.add(shoppingCart);

        var shoppingCartFound = shoppingCartPersistenceProvider.ofCustomer(DEFAULT_CUSTOMER_ID).orElseThrow();

        assertThat(shoppingCartFound).isNotNull();
        assertThat(shoppingCartFound.customerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(shoppingCartFound.id()).isEqualTo(shoppingCart.id());
    }

    @Test
    void shouldRemoveShoppingCartById() {
        var shoppingCart = aShoppingCart().build();
        shoppingCartPersistenceProvider.add(shoppingCart);
        assertThat(shoppingCartPersistenceProvider.exists(shoppingCart.id())).isTrue();

        shoppingCartPersistenceProvider.remove(shoppingCart.id());

        assertThat(shoppingCartPersistenceProvider.exists(shoppingCart.id())).isFalse();
        assertThat(entityRepository.findById(shoppingCart.id().value())).isEmpty();
    }

    @Test
    void shouldRemoveShoppingCartByEntity() {
        var shoppingCart = aShoppingCart().build();
        shoppingCartPersistenceProvider.add(shoppingCart);
        assertThat(shoppingCartPersistenceProvider.exists(shoppingCart.id())).isTrue();

        shoppingCartPersistenceProvider.remove(shoppingCart);

        assertThat(shoppingCartPersistenceProvider.exists(shoppingCart.id())).isFalse();
    }

}
