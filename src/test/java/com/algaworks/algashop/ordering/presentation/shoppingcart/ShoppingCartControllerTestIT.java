package com.algaworks.algashop.ordering.presentation.shoppingcart;

import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.presentation.AbstractPresentationIT;
import com.algaworks.algashop.ordering.presentation.order.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShoppingCartControllerTestIT extends AbstractPresentationIT {

    @LocalServerPort
    private int port;

    private final ShoppingCartPersistenceEntityRepository shoppingCartRepository;

    private static final UUID validShoppingCartId = UUID.fromString("4f31582a-66e6-4601-a9d3-ff608c2d4461");

    @BeforeEach
    public void setup() {
        super.beforeEach();
    }

    @BeforeAll
    public static void setupBeforeAll() {
        initWireMock();
    }

    @AfterAll
    public static void afterAll() {
        stopMock();
    }

    @Test
    public void shouldCreateShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/shoppingcart/create-shopping-cart.json");
        UUID createdShoppingCart = RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/shopping-carts")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString()))
                .extract()
                .jsonPath().getUUID("id");

        assertThat(shoppingCartRepository.existsById(createdShoppingCart)).isTrue();
    }

    @Test
    public void shouldAddProductToShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/shoppingcart/add-product-to-shopping-cart.json");

        RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/shopping-carts/{shoppingCartId}/items", validShoppingCartId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        var shoppingCartPersistenceEntity = shoppingCartRepository.findById(validShoppingCartId).orElseThrow();
        assertThat(shoppingCartPersistenceEntity.getTotalItems()).isEqualTo(4);
    }

}
