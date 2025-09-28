package com.algaworks.algashop.ordering.application.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.entity.fixture.ShoppingCartTestFixture;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShoppingCartManagementApplicationServiceTest {


    @Test
    void shouldAddItemToCart_whenProductIsInStock() {
        var shoppingCart = ShoppingCartTestFixture.aShoppingCart().build();
    }

}