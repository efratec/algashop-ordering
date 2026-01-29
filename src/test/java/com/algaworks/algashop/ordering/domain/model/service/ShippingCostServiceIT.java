package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.order.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService.CalculationRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

@SpringBootTest
class ShippingCostServiceIT {

    @Autowired
    private ShippingCostService shippingCostService;

    @Autowired
    private OriginAddressService originAddressService;

    private WireMockServer wireMockRapidex;

    @BeforeEach
    public void setup() {
        wireMockRapidex = new WireMockServer(options()
                .port(8780)
                .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex")
                .extensions(new ResponseTemplateTransformer(true)));

        wireMockRapidex.start();
    }
    @AfterEach
    public void clean() {
        wireMockRapidex.stop();
    }


    @Test
    void shouldCalculate() {
        var origin = originAddressService.originAddress().zipCode();
        var destination = ZipCode.of("12345");

        var calculate = shippingCostService.calculate(new CalculationRequest(origin, destination));

        assertThat(calculate.cost()).isNotNull();
        assertThat(calculate.expectedDate()).isNotNull();
    }

}