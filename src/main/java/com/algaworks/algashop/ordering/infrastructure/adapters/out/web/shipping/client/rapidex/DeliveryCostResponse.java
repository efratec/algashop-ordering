package com.algaworks.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCostResponse implements Serializable {

    private String deliveryCost;
    private Long estimatedDaysToDeliver;

}
