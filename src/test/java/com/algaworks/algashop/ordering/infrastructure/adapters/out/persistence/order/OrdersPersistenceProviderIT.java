package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.order;

import com.algaworks.algashop.ordering.core.domain.model.order.OrderTestFixture;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderStatusEnum;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.AbstractPersistenceIT;
import com.algaworks.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomersPersistenceProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestFixture.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestFixture.existingCustomer;
import static org.assertj.core.api.Assertions.assertThat;

@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssembler.class,
        OrderPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrdersPersistenceProviderIT extends AbstractPersistenceIT {

    private final OrdersPersistenceProvider persistenceProvider;
    private final OrderPersistenceEntityRepository entityRepository;
    private final CustomersPersistenceProvider customersPersistenceProvider;

    @BeforeEach
    void setup() {
        if (!customersPersistenceProvider.exists(DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(existingCustomer().build());
        }
    }

    @Test
    void shouldUpdateAndKeepPersistenceEntityState() {
        var order = OrderTestFixture.anOrder().status(OrderStatusEnum.PLACED).build();
        long orderId = order.id().value().toLong();
        persistenceProvider.add(order);

        var persistenceEntity = entityRepository.findById(orderId).orElseThrow();

        assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatusEnum.PLACED.name());
        assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();

        order = persistenceProvider.ofId(order.id()).orElseThrow();
        order.markAsPaid();
        persistenceProvider.add(order);

        persistenceEntity = entityRepository.findById(orderId).orElseThrow();
        assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatusEnum.PAID.name());
        assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
    }

}
