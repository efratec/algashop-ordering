package com.algaworks.algashop.ordering.infrastructure.shipping.client.rapidex;

import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "RAPIDEX")
public class ShippingCostServiceRapidexImpl implements ShippingCostService {

    private final RapidexApiClient rapidexApiClient;

    @Override
    public CalculationResult calculate(CalculationRequest request) {
       var response = rapidexApiClient.calculate(
               new DeliveryCostRequest(request.origin().value(),
                       request.destination().value()));

        var expectedDeliveryDate =  LocalDate.now().plusDays(response.getEstimatedDaysToDeliver());

       return CalculationResult.builder()
               .cost(Money.of(response.getDeliveryCost()))
               .expectedDate(expectedDeliveryDate)
               .build();
    }

}
