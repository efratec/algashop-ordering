package com.algaworks.algashop.ordering.infrastructure.product.client.http;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.presentation.BadGatewayException;
import com.algaworks.algashop.ordering.presentation.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCatalogServierHttpImpl implements ProductCatalogService {

    private final ProductCatalogAPIClient productCatalogAPIClient;

    @Override
    public Optional<Product> ofId(ProductId productId) {
        ProductResponse productResponse = null;
        try {
            productResponse = productCatalogAPIClient.getById(productId.value());
        } catch (ResourceAccessException e) {
            GatewayTimeoutException.of("Product Catalog API Timeout", e);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            BadGatewayException.of("Product Catalog API Bad Gateway", e);
        }

        return Optional.of(
                Product.builder()
                        .id(ProductId.from(productResponse.getId()))
                        .name(ProductName.of(productResponse.getName()))
                        .inStock(productResponse.getInStock())
                        .price(new Money(productResponse.getSalePrice()))
                        .build()
        );
    }

}
