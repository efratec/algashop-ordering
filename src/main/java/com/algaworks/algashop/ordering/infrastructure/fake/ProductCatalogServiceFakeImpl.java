package com.algaworks.algashop.ordering.infrastructure.fake;

import com.algaworks.algashop.ordering.domain.model.service.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;

import java.util.Optional;

//TODO: this is a temporary class
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
