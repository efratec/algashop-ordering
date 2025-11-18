package com.algaworks.algashop.ordering.infrastructure.beans;

import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.order.CustomerHaveFreeShippingSpecification;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpecificationBeansConfig {

    private static final int minPointsForFreeShippingRule1 = 200;
    private static final long salesQuantityForFreeSHippingRule1 = 2L;
    private static final int minPointsForFreeShippingRule2 = 2000;

    @Bean
    public CustomerHaveFreeShippingSpecification customerHasFreeShippingSpecification(Orders orders) {
        return new CustomerHaveFreeShippingSpecification(
                orders,
                LoyaltyPoints.of(minPointsForFreeShippingRule1),
                salesQuantityForFreeSHippingRule1,
                LoyaltyPoints.of(minPointsForFreeShippingRule2));
    }

}
