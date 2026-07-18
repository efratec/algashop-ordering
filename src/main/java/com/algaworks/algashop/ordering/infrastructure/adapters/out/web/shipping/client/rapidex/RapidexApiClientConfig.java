package com.algaworks.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class RapidexApiClientConfig {

    @Bean
    public RapidexApiClient rapidexApiClient(RestClient.Builder builder,
                                             @Value("${algashop.integrations.rapidex.url}") String rapiDexUrl) {

        var restClient = builder.baseUrl(rapiDexUrl)
                .requestFactory(generateClientHttpRequestFactory())
                .build();
        var adapter = RestClientAdapter.create(restClient);
        var proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return proxyFactory.createClient(RapidexApiClient.class);
    }

    private ClientHttpRequestFactory generateClientHttpRequestFactory() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(10));
        factory.setConnectTimeout(Duration.ofSeconds(7));
        return factory;
    }

}
