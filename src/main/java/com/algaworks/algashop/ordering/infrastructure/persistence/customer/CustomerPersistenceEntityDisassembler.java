package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import org.springframework.stereotype.Component;

import static com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityDisassembler.convertAddressEmbeddableToAddress;

@Component
public class CustomerPersistenceEntityDisassembler {

    public static CustomerPersistenceEntityDisassembler of(){
        return new CustomerPersistenceEntityDisassembler();
    }

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
