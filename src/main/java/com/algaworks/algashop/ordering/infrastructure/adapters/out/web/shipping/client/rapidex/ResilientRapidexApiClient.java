package com.algaworks.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import com.algaworks.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.algaworks.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreaker;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfig;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.core.retry.RetryException;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;

@Component
@Slf4j
public class ResilientRapidexApiClient {

    private final RapidexApiClient rapidexApiClient;
    private final FrameworkRetryCircuitBreaker circuitBreaker;

    public ResilientRapidexApiClient(RapidexApiClient rapidexApiClient,
                                     CircuitBreakerFactory<FrameworkRetryConfig,
                                             FrameworkRetryConfigBuilder> circuitBreakerFactory) {
        this.rapidexApiClient = rapidexApiClient;
        this.circuitBreaker = (FrameworkRetryCircuitBreaker) circuitBreakerFactory.create("rapidexApiCB");
    }

    @ConcurrencyLimit(10)
    public DeliveryCostResponse calculate(DeliveryCostRequest request) {
        circuitBreaker.getCircuitBreakerPolicy().canRetry();
        log.info("Rapidex Api CB state is {}", circuitBreaker.getCircuitBreakerPolicy().getState());
        try {
            DeliveryCostResponse response = circuitBreaker.run(() -> doCalculate(request));
            if (response == null) {
                throw new BadGatewayException.ClientErrorException("Invalid zip code provided");
            }
            return response;
        } catch (NoFallbackAvailableException e) {
            throw unwrapException(e);
        }
    }

    private DeliveryCostResponse doCalculate(DeliveryCostRequest deliveryCostRequest) {
        log.info("Loading rapidex api {}", deliveryCostRequest);
        try {
            return rapidexApiClient.calculate(deliveryCostRequest);
        } catch (HttpClientErrorException e) {
            if (!(e instanceof HttpClientErrorException.NotFound)) {
                log.error("Client HTTP error when loading delivery cost {}", deliveryCostRequest, e);
            }
            return null;
        } catch (RestClientException e) {
            throw translateException(e);
        }
    }

    private RuntimeException unwrapException(NoFallbackAvailableException e) {
        if (e.getCause() instanceof RetryException re) {
            if (re.getCause() instanceof GatewayTimeoutException gte) {
                return gte;
            }
            if (re.getCause() instanceof BadGatewayException bge) {
                return bge;
            }
        }
        return e;
    }

    private RuntimeException translateException(RestClientException error) {
        if (error.getCause() instanceof SocketTimeoutException
                || error instanceof ResourceAccessException) {
            return new GatewayTimeoutException("Rapidex API Timeout", error);
        }

        if (error instanceof HttpClientErrorException) {
            return new BadGatewayException.ClientErrorException("Rapidex API Bad Gateway", error);
        }

        if (error instanceof HttpServerErrorException) {
            return new BadGatewayException.ServerErrorException("Rapidex API Bad Gateway", error);
        }

        return new BadGatewayException("Rapidex API Bad Gateway", error);
    }


}
