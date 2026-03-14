package com.algaworks.algashop.ordering.infrastructure.adapters.in.web.order;

import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.algaworks.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utils.AlgaShopResourceUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrderControllerTestIT extends AbstractPresentationIT {

    @LocalServerPort
    private int port;

    @Autowired
    private OrderPersistenceEntityRepository orderRepository;

    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    private WireMockServer wireMockProductCatalog;
    private WireMockServer wireMockRapidex;

    @BeforeEach
    public void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        wireMockRapidex = new WireMockServer(options()
                .port(8780)
                .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex")
                .extensions(new ResponseTemplateTransformer(true)));

        wireMockProductCatalog = new WireMockServer(options()
                .port(8781)
                .usingFilesUnderDirectory("src/test/resources/wiremock/product-catalog")
                .extensions(new ResponseTemplateTransformer(true)));

        wireMockRapidex.start();
        wireMockProductCatalog.start();
    }

    @AfterEach
    public void after() {
        wireMockRapidex.stop();
        wireMockProductCatalog.stop();
    }

    @Test
    public void shouldCreateOrderUsingProduct() {
        String json = AlgaShopResourceUtils.readContent("json/order/create-order-with-product.json");

        String createdOrderId = RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
                .when()
                .post("/api/v1/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString()),
                        "customer.id", Matchers.is(validCustomerId.toString()))
                .extract()
                .jsonPath().getString("id");

        boolean orderExists = orderRepository.existsById(OrderId.from(createdOrderId).value().toLong());
        assertThat(orderExists).isTrue();

    }

    @Test
    public void shouldNotCreateOrderUsingProductWhenProductNotExists() {
        String json = AlgaShopResourceUtils.readContent("json/order/create-order-with-invalid-product.json");

        RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
                .when()
                .post("/api/v1/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

    }

    @Test
    public void shouldNotCreateOrderUsingProductWhenCustomerWasNotFound() {
        String json = AlgaShopResourceUtils.readContent("json/order/create-order-with-product-and-invalid-customer.json");
        RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
                .when()
                .post("/api/v1/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void shouldCreateOrderUsingShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/order/create-order-with-shopping-cart.json");

        var orderDetailOutput = RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json)
                .when()
                .post("/api/v1/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString()),
                        "customer.id", Matchers.is(validCustomerId.toString()))
                .extract()
                .body().as(OrderDetailOutput.class);

        assertThat(orderDetailOutput.getCustomer().getId()).isEqualTo(validCustomerId);

        boolean orderExists = orderRepository.existsById(orderDetailOutput.getId().value().toLong());
        assertThat(orderExists).isTrue();
    }

}
