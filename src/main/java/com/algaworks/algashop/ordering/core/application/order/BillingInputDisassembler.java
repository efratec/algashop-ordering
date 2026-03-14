package com.algaworks.algashop.ordering.core.application.order;

import com.algaworks.algashop.ordering.core.ports.in.order.BillingData;
import com.algaworks.algashop.ordering.core.domain.model.commons.*;
import com.algaworks.algashop.ordering.core.domain.model.order.Billing;
import org.springframework.stereotype.Component;

@Component
public class BillingInputDisassembler {

    public Billing toDomainModel(BillingData billingData) {
        var address = billingData.getAddress();
        return Billing.builder()
                .fullName(FullName.of(billingData.getFirstName(), billingData.getLastName()))
                .document(Document.of(billingData.getDocument()))
                .phone(Phone.of(billingData.getPhone()))
                .email(Email.of(billingData.getEmail()))
                .address(Address.builder()
                        .street(address.getStreet())
                        .number(address.getNumber())
                        .complement(address.getComplement())
                        .neighborhood(address.getNeighborhood())
                        .city(address.getCity())
                        .state(address.getState())
                        .zipCode(ZipCode.of(address.getZipCode()))
                        .build())
                .build();
    }

}
