package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartUpdateProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.existingCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProduct;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProductAltRamMemory;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ShoppingCartTestFixture.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        ShoppingCartUpdateProvider.class,
        ShoppingCartPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingCartsUpdateProviderIT {

    private final ShoppingCartPersistenceProvider persistenceProvider;
    private final CustomersPersistenceProvider customersPersistenceProvider;
    private final ShoppingCartPersistenceEntityRepository entityRepository;
    private final ShoppingCartUpdateProvider shoppingCartUpdateProvider;

    @BeforeEach
    void setup() {
        if (!customersPersistenceProvider.exists(DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(existingCustomer().build());
        }
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void shouldUpdateItemPriceAndTotalAmount() {
        final var shoppingCart = aShoppingCart().withItems(false).build();

        final var productT1 = aProduct().price(Money.of("2000")).build();
        final var productT2 = aProductAltRamMemory().price(Money.of("200")).build();

        shoppingCart.addItem(productT1, Quantity.of(2));
        shoppingCart.addItem(productT2, Quantity.of(1));

        persistenceProvider.add(shoppingCart);

        final var productIdToUpdate = productT1.id();
        final var newProductT1Price = Money.of("1500");
        final var expectedNewItemTotalPrice = newProductT1Price.multiply(Quantity.of(2));
        final var expectedNewCartTotalAmount = expectedNewItemTotalPrice.add(Money.of("200"));

        shoppingCartUpdateProvider.adjustPrice(productIdToUpdate, newProductT1Price);

        final var updatedShoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();

        assertThat(updatedShoppingCart.totalAmount()).isEqualTo(expectedNewCartTotalAmount);
        assertThat(updatedShoppingCart.totalItems()).isEqualTo(Quantity.of(3));

        final var item = updatedShoppingCart.findItem(productIdToUpdate);
        assertThat(item.totalAmount()).isEqualTo(expectedNewItemTotalPrice);
        assertThat(item.price()).isEqualTo(newProductT1Price);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
            void shouldUpdateItemAvailability() {
        final var shoppingCart = aShoppingCart().withItems(false).build();

        final var product1 = aProduct()
                .price(Money.of("2000"))
                .inStock(true).build();
        final var product2 = aProductAltRamMemory()
                .price(Money.of("200"))
                .inStock(true).build();

        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));

        persistenceProvider.add(shoppingCart);

        final var productIdToUpdate = product1.id();
        final var productIdNotToUpdate = product2.id();

        shoppingCartUpdateProvider.changeAvailability(productIdToUpdate, false);

        final var updatedShoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();

        final var item = updatedShoppingCart.findItem(productIdToUpdate);

        assertThat(item.isAvailable()).isFalse();

        final var item2 = updatedShoppingCart.findItem(productIdNotToUpdate);

        assertThat(item2.isAvailable()).isTrue();
    }


}
