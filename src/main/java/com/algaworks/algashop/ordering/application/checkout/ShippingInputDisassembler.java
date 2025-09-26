package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService.CalculationResult;
import org.springframework.stereotype.Component;

@Component
public class ShippingInputDisassembler {

    public Shipping toDomainModel(ShippingInput shippingInput,
                                  CalculationResult shippingCalculationResult) {

        var addressData = shippingInput.getAddress();
        return Shipping.builder()
                .cost(shippingCalculationResult.cost())
                .expectedDate(shippingCalculationResult.expectedDate())
                .recipient(Recipient.builder()
                        .fullName(FullName.of(
                                shippingInput.getRecipient().getFirstName(),
                                shippingInput.getRecipient().getLastName()))
                        .document(Document.of(shippingInput.getRecipient().getDocument()))
                        .phone(Phone.of(shippingInput.getRecipient().getPhone()))
                        .build())
                .address(Address.builder()
                        .street(addressData.getStreet())
                        .number(addressData.getNumber())
                        .complement(addressData.getComplement())
                        .neighborhood(addressData.getNeighborhood())
                        .city(addressData.getCity())
                        .state(addressData.getState())
                        .zipCode(ZipCode.of(addressData.getZipCode()))
                        .build())
                .build();
    }

}
