package com.algaworks.algashop.ordering.infrastructure.adapters.out.web.product.client.fake;

import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.product.Product;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductName;

import java.util.Optional;

//@Component
public class ProductCatalogServiceFakeImpl implements ProductCatalogService {

    @Override
    public Optional<Product> ofId(ProductId productId) {
        return Optional.of(Product.builder()
                .id(productId)
                .inStock(true)
                .name(ProductName.of("Notebook"))
                .price(Money.of("3000"))
                .build());
    }

}
