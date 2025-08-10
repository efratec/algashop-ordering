package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.stereotype.Component;

import static com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler.convertAddressEmbeddableToAddress;

@Component
public class CustomerPersistenceEntityDisassembler {

    public Customer toDomainEntity(CustomerPersistenceEntity persistenceEntity) {
        return Customer.existing()
                .id(CustomerId.from(persistenceEntity.getId()))
                .fullName(FullName.of(persistenceEntity.getFirstName(), persistenceEntity.getLastName()))
                .email(Email.of(persistenceEntity.getEmail()))
                .phone(Phone.of(persistenceEntity.getPhone()))
                .birthDate(BirthDate.of(persistenceEntity.getBirthDate()))
                .document(Document.of(persistenceEntity.getDocument()))
                .promotionNotificationsAllowed(persistenceEntity.getPromotionNotificationsAllowed())
                .archived(persistenceEntity.getArchived())
                .registeredAt(persistenceEntity.getRegisteredAt())
                .archivedAt(persistenceEntity.getArchivedAt())
                .address(convertAddressEmbeddableToAddress(persistenceEntity.getAddress()))
                .loyaltyPoints(LoyaltyPoints.of(persistenceEntity.getLoyaltyPoints()))
                .version(persistenceEntity.getVersion())
                .build();
    }

}
