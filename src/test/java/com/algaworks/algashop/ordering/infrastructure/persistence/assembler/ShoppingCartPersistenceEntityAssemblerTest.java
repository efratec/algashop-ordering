package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestFixture;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ShoppingCartTestFixture.aShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ShoppingCartPersistenceEntityAssemblerTest {

    @Mock
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @InjectMocks
    private ShoppingCartPersistenceEntityAssembler assembler;

    @BeforeEach
    void setup() {
        Mockito.when(customerPersistenceEntityRepository.getReferenceById(Mockito.any(UUID.class)))
                .then(a -> {
                    var customerId = a.getArgument(0, UUID.class);
                    return CustomerPersistenceEntityTestFixture.aCustomer().id(customerId).build();
                });
    }

    @Test
    void shouldConvertToDomainShoppingCart() {
        var shoppingCart = aShoppingCart().withItems(true).build();
        var shoppingCartPersistenceEntity = assembler.fromDomain(shoppingCart);

        assertThat(shoppingCartPersistenceEntity).satisfies(shoppingCartEntity -> {
            assertThat(shoppingCartEntity.getCustomer().getId()).isEqualTo(shoppingCart.customerId().value());
            assertThat(shoppingCartEntity.getTotalItems()).isEqualTo(shoppingCart.totalItems().value());
            assertThat(shoppingCartEntity.getItems()).hasSameSizeAs(shoppingCart.items());
            assertThat(shoppingCartEntity.getCreatedAt()).isNotNull();
        });
    }

}
