package com.algaworks.algashop.ordering.application.commons.mappers;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring",
        uses = {AddressMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerMapper {

    @Mappings({
            @Mapping(target = "id",        expression = "java(customer.id() != null ? customer.id().value() : null)"),
            @Mapping(target = "firstName", expression = "java(customer.fullName() != null ? customer.fullName().firstName() : null)"),
            @Mapping(target = "lastName",  expression = "java(customer.fullName() != null ? customer.fullName().lastName() : null)"),
            @Mapping(target = "email",     expression = "java(customer.email() != null ? customer.email().value() : null)"),
            @Mapping(target = "document",  expression = "java(customer.document() != null ? customer.document().value() : null)"),
            @Mapping(target = "phone",     expression = "java(customer.phone() != null ? customer.phone().value() : null)"),
            @Mapping(target = "birthDate", expression = "java(customer.birthDate() != null ? customer.birthDate().value() : null)"),
            @Mapping(target = "loyaltyPoints", expression = "java(customer.loyaltyPoints() != null ? customer.loyaltyPoints().value() : 0)"),
            @Mapping(target = "registeredAt", expression = "java(customer.registeredAt())"),
            @Mapping(target = "archivedAt",   expression = "java(customer.archivedAt())"),
            @Mapping(target = "promotionNotificationsAllowed", source = "promotionNotificationsAllowed"),
            @Mapping(target = "archived", expression = "java(customer.isArchived())"),
            @Mapping(target = "address",  expression = "java(mapAddress(customer.address()))")
    })
    CustomerOutput toOutput(Customer customer);

    // Coleções (útil em listagens)
    List<CustomerOutput> toOutputList(List<Customer> customers);

    // ---- Helpers para extrair valores de VOs ----
    @Named("idToUuid")
    default UUID idToUuid(CustomerId id) {
        if (id == null) return null;
        // ajuste se seu CustomerId expõe "value()" ou "uuid()"
        return id.value(); // ou UUID.fromString(id.value().toString())
    }

    @Named("firstName")
    default String firstName(FullName fullName) {
        return fullName != null ? fullName.firstName() : null;
    }

    @Named("lastName")
    default String lastName(FullName fullName) {
        return fullName != null ? fullName.lastName() : null;
    }

    @Named("emailValue")
    default String emailValue(Email email) {
        return email != null ? email.value() : null; // ajuste se for email.address() / toString()
    }

    @Named("documentValue")
    default String documentValue(Document document) {
        return document != null ? document.value() : null;
    }

    @Named("phoneValue")
    default String phoneValue(Phone phone) {
        return phone != null ? phone.value() : null;
    }

    @Named("birthDateValue")
    default LocalDate birthDateValue(BirthDate birthDate) {
        return birthDate != null ? birthDate.value() : null;
    }

    @Named("loyaltyPointsValue")
    default Integer loyaltyPointsValue(LoyaltyPoints loyaltyPoints) {
        return loyaltyPoints != null ? loyaltyPoints.value() : 0;
    }

    AddressData map(Address address);

    // Wrapper null-safe para usar em expression
    default AddressData mapAddress(Address a) {
        return a == null ? null : map(a);
    }

}
