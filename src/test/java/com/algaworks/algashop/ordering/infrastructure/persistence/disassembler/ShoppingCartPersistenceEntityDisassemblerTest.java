package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import static com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntityTestFixture.existingShoppingCart;
import static org.assertj.core.api.Assertions.assertThat;

@Component
class ShoppingCartPersistenceEntityDisassemblerTest {

  private final ShoppingCartPersistenceEntityDisassembler disassembler = ShoppingCartPersistenceEntityDisassembler.of();

  @Test
  void shouldConverFromPersistence() {
      var persistenceEntity = existingShoppingCart().build();
      var domainEntity = disassembler.toDomainEntity(persistenceEntity);
      assertThat(domainEntity).satisfies(entity -> {
          assertThat(entity.id().value()).isEqualTo(persistenceEntity.getId());
          assertThat(entity.totalItems().value()).isEqualTo(persistenceEntity.getTotalItems());
          assertThat(entity.customerId().value()).isEqualTo(persistenceEntity.getCustomerId());
          assertThat(entity.totalAmount().value()).isEqualTo(persistenceEntity.getTotalAmount());
          assertThat(entity.items()).hasSameSizeAs(persistenceEntity.getItems());
      });
  }

}
