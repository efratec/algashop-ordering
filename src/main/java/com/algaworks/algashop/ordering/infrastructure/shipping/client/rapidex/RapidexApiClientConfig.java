package com.algaworks.algashop.ordering.infrastructure.shipping.client.rapidex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RapidexApiClientConfig {

    @Bean
    public RapidexApiClient rapidexApiClient(RestClient.Builder builder,
                                             @Value("${algashop.integrations.rapidex.url}") String rapiDexUrl) {

        var restClient = builder.baseUrl(rapiDexUrl).build();
        var adapter = RestClientAdapter.create(restClient);
        var proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return proxyFactory.createClient(RapidexApiClient.class);
    }


}
