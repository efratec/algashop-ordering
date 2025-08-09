package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderPersistenceEntityTestFixture.existingOrder;
import static com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler.convertBillingEmbeddableToBilling;
import static com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler.convertShippingEmbeddableToShipping;
import static org.assertj.core.api.Assertions.assertThat;

class OrderPersistenceEntityDisassemblerTest {

    private final OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

    @Test
    void shouldConvertFromPersistence() {
        var persistenceEntity = existingOrder().build();
        var domainEntity = disassembler.toDomainEntity(persistenceEntity);
        assertThat(domainEntity).satisfies(
                s -> assertThat(s.id()).isEqualTo(OrderId.from(persistenceEntity.getId())),
                s -> assertThat(s.customerId()).isEqualTo(CustomerId.from(persistenceEntity.getCustomerId())),
                s -> assertThat(s.totalAmount()).isEqualTo(Money.of(persistenceEntity.getTotalAmount())),
                s -> assertThat(s.totalItems()).isEqualTo(Quantity.of(persistenceEntity.getTotalItems())),
                s -> assertThat(s.placedAt()).isEqualTo(persistenceEntity.getPlacedAt()),
                s -> assertThat(s.paidAt()).isEqualTo(persistenceEntity.getPaidAt()),
                s -> assertThat(s.canceledAt()).isEqualTo(persistenceEntity.getCanceledAt()),
                s -> assertThat(s.readyAt()).isEqualTo(persistenceEntity.getReadyAt()),
                s -> assertThat(s.status()).isEqualTo(OrderStatusEnum.valueOf(persistenceEntity.getStatus())),
                s -> assertThat(s.paymentMethod()).isEqualTo(PaymentMethodEnum.valueOf(persistenceEntity.getPaymentMethod())),
                s -> assertThat(s.billing()).isEqualTo(convertBillingEmbeddableToBilling(persistenceEntity.getBilling())),
                s -> assertThat(s.shipping()).isEqualTo(convertShippingEmbeddableToShipping(persistenceEntity.getShipping()))
        );
    }

}