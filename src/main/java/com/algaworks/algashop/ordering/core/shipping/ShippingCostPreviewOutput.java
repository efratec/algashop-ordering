package com.algaworks.algashop.ordering.core.shipping;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ShippingCostPreviewOutput {

    private BigDecimal cost;
    private LocalDate expectedDate;

}
