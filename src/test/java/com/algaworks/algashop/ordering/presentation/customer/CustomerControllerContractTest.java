package com.algaworks.algashop.ordering.presentation.customer;

import com.algaworks.algashop.ordering.application.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.application.customer.management.CustomerUpdateInput;
import com.algaworks.algashop.ordering.application.customer.query.*;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.algaworks.algashop.ordering.domain.exception.DomainException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CustomerController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomerControllerContractTest {

    private final WebApplicationContext context;

    @MockitoBean
    private CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoBean
    private CustomerQueryService customerQueryService;

    @MockitoBean
    private ShoppingCartQueryService shoppingCartQueryService;

    @BeforeEach
    public void setupAll() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void createCustomerContract() {
        var customerOutput = CustomerOutputTestFixture.existing().build();
        var customerId = UUID.randomUUID();

        when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
                .thenReturn(customerId);
        when(customerQueryService.findById(Mockito.any(UUID.class)))
                .thenReturn(customerOutput);

        String jsonInput = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1991-07-05",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", Matchers.containsString("/api/v1/customers/" + customerId))
                .body(
                        "id", Matchers.notNullValue(),
                        "registeredAt", Matchers.notNullValue(),
                        "firstName", Matchers.is("John"),
                        "lastName", Matchers.is("Doe"),
                        "email", Matchers.is("johndoe@email.com"),
                        "document", Matchers.is("12345"),
                        "phone", Matchers.is("1191234564"),
                        "birthDate", Matchers.is("1991-07-05"),
                        "promotionNotificationsAllowed", Matchers.is(false),
                        "loyaltyPoints", Matchers.is(0),
                        "address.street", Matchers.is("Bourbon Street"),
                        "address.number", Matchers.is("2000"),
                        "address.complement", Matchers.is("apt 122"),
                        "address.neighborhood", Matchers.is("North Ville"),
                        "address.city", Matchers.is("Yostfort"),
                        "address.state", Matchers.is("South Carolina"),
                        "address.zipCode", Matchers.is("12321")
                );
    }

    @Test
    public void createCustomerErrorContract() {
        String jsonInput = """
                {
                  "firstName": "",
                  "lastName": "",
                  "email": "johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1991-07-05",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonInput)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(
                        "status", Matchers.is(HttpStatus.BAD_REQUEST.value()),
                        "type", Matchers.is("/errors/invalid-fields"),
                        "title", Matchers.notNullValue(),
                        "detail", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue(),
                        "fields", Matchers.notNullValue()
                );
    }

    @Test
    public void findCustomersContract() {
        int sizeLimit = 5;
        int pageNumber = 0;

        var customer1 = CustomerSummaryOutputTestFixture.existing().build();
        var customer2 = CustomerSummaryOutputTestFixture.existingAlt1().build();

        when(customerQueryService.filter(Mockito.any(CustomerFilter.class)))
                .thenReturn(new PageImpl<>(List.of(customer1, customer2)));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("size", sizeLimit)
                .queryParam("page", pageNumber)
                .when()
                .get("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "number", Matchers.equalTo(pageNumber),
                        "size", Matchers.equalTo(2),
                        "totalPages", Matchers.equalTo(1),
                        "totalElements", Matchers.equalTo(2),

                        "content[0].id", Matchers.equalTo(customer1.getId().toString()),
                        "content[0].firstName", Matchers.is(customer1.getFirstName()),
                        "content[0].lastName", Matchers.is(customer1.getLastName()),
                        "content[0].email", Matchers.is(customer1.getEmail()),
                        "content[0].document", Matchers.is(customer1.getDocument()),
                        "content[0].phone", Matchers.is(customer1.getPhone()),
                        "content[0].birthDate", Matchers.is(customer1.getBirthDate().toString()),
                        "content[0].loyaltyPoints", Matchers.is(customer1.getLoyaltyPoints()),
                        "content[0].promotionNotificationsAllowed", Matchers.is(customer1.getPromotionNotificationsAllowed()),
                        "content[0].archived", Matchers.is(customer1.getArchived()),
                        "content[0].registeredAt", Matchers.is(formatter.format(customer1.getRegisteredAt())),

                        "content[1].id", Matchers.equalTo(customer2.getId().toString()),
                        "content[1].firstName", Matchers.is(customer2.getFirstName()),
                        "content[1].lastName", Matchers.is(customer2.getLastName()),
                        "content[1].email", Matchers.is(customer2.getEmail()),
                        "content[1].document", Matchers.is(customer2.getDocument()),
                        "content[1].phone", Matchers.is(customer2.getPhone()),
                        "content[1].birthDate", Matchers.is(customer2.getBirthDate().toString()),
                        "content[1].loyaltyPoints", Matchers.is(customer2.getLoyaltyPoints()),
                        "content[1].promotionNotificationsAllowed", Matchers.is(customer2.getPromotionNotificationsAllowed()),
                        "content[1].archived", Matchers.is(customer2.getArchived()),
                        "content[1].registeredAt", Matchers.is(formatter.format(customer2.getRegisteredAt()))

                );
    }

    @Test
    public void findByIdContract() {
        CustomerOutput customer = CustomerOutputTestFixture.existing().build();

        when(customerQueryService.findById(customer.getId())).thenReturn(customer);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        var address = customer.getAddress();

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get("/api/v1/customers/{customerId}", customer.getId())
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "id", Matchers.equalTo(customer.getId().toString()),
                        "firstName", Matchers.equalTo(customer.getFirstName()),
                        "lastName", Matchers.is(customer.getLastName()),
                        "email", Matchers.is(customer.getEmail()),
                        "document", Matchers.is(customer.getDocument()),
                        "phone", Matchers.is(customer.getPhone()),
                        "birthDate", Matchers.is(customer.getBirthDate().toString()),
                        "loyaltyPoints", Matchers.is(customer.getLoyaltyPoints()),
                        "promotionNotificationsAllowed", Matchers.is(customer.getPromotionNotificationsAllowed()),
                        "archived", Matchers.is(customer.getArchived()),
                        "registeredAt", Matchers.is(formatter.format(customer.getRegisteredAt())),
                        "address.street", Matchers.is(address.getStreet()),
                        "address.number", Matchers.is(address.getNumber()),
                        "address.complement", Matchers.is(address.getComplement()),
                        "address.neighborhood", Matchers.is(address.getNeighborhood()),
                        "address.city", Matchers.is(address.getCity()),
                        "address.state", Matchers.is(address.getState()),
                        "address.zipCode", Matchers.is(address.getZipCode())
                );
    }

    @Test
    public void updateCustomerContract() {
        var customerOutPut = CustomerOutputTestFixture.existing().build();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        var address = customerOutPut.getAddress();

        UUID customerId = UUID.randomUUID();
        when(customerQueryService.findById(Mockito.any(UUID.class)))
                .thenReturn(customerOutPut);

        final String jsonInput = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "phone": "1191234564",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;
        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/v1/customers/{customerId}", customerId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "id", Matchers.equalTo(customerOutPut.getId().toString()),
                        "firstName", Matchers.equalTo(customerOutPut.getFirstName()),
                        "lastName", Matchers.is(customerOutPut.getLastName()),
                        "email", Matchers.is(customerOutPut.getEmail()),
                        "document", Matchers.is(customerOutPut.getDocument()),
                        "phone", Matchers.is(customerOutPut.getPhone()),
                        "birthDate", Matchers.is(customerOutPut.getBirthDate().toString()),
                        "loyaltyPoints", Matchers.is(customerOutPut.getLoyaltyPoints()),
                        "promotionNotificationsAllowed", Matchers.is(customerOutPut.getPromotionNotificationsAllowed()),
                        "archived", Matchers.is(customerOutPut.getArchived()),
                        "registeredAt", Matchers.is(formatter.format(customerOutPut.getRegisteredAt())),
                        "address.street", Matchers.is(address.getStreet()),
                        "address.number", Matchers.is(address.getNumber()),
                        "address.complement", Matchers.is(address.getComplement()),
                        "address.neighborhood", Matchers.is(address.getNeighborhood()),
                        "address.city", Matchers.is(address.getCity()),
                        "address.state", Matchers.is(address.getState()),
                        "address.zipCode", Matchers.is(address.getZipCode())
                );
    }

    @Test
    public void deleteCustomerContract() {
        Mockito.doNothing().when(customerManagementApplicationService)
                .archive(Mockito.any(UUID.class));

        UUID customerId = UUID.randomUUID();

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/v1/customers/{customerId}", customerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void findByIdError404Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        when(customerQueryService.findById(invalidCustomerId))
                .thenThrow(CustomerNotFoundException.class);

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get("/api/v1/customers/{customerId}", invalidCustomerId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(
                        "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
                        "type", Matchers.is("/errors/not-found"),
                        "title", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue()
                );
    }

    @Test
    public void createCustomerError409Contract() {
        when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
                .thenThrow(CustomerEmailIsInUseException.class);

        String jsonInput = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1991-07-05",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.CONFLICT.value())
                .body(
                        "status", Matchers.is(HttpStatus.CONFLICT.value()),
                        "type", Matchers.is("/errors/conflict"),
                        "title", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue()
                );
    }

    @Test
    public void createCustomerError422Contract() {
        when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
                .thenThrow(DomainException.class);

        String jsonInput = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1991-07-05",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body(
                        "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                        "type", Matchers.is("/errors/unprocessable-entity"),
                        "title", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue()
                );
    }

    @Test
    public void createCustomerError500Contract() {
        when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
                .thenThrow(RuntimeException.class);

        String jsonInput = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1991-07-05",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(
                        "status", Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "type", Matchers.is("/errors/internal"),
                        "title", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue()
                );
    }

    @Test
    public void updateCustomerErrorContract() {
        final String jsonInput = """
                {
                  "firstName": "",
                  "lastName": "",
                  "phone": "",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonInput)
                .when()
                .put("/api/v1/customers/{customerId}", UUID.randomUUID())
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(
                        "status", Matchers.is(HttpStatus.BAD_REQUEST.value()),
                        "type", Matchers.is("/errors/invalid-fields"),
                        "title", Matchers.notNullValue(),
                        "detail", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue(),
                        "fields", Matchers.notNullValue()
                );
    }

    @Test
    void updateCustomerError404Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        when(customerQueryService.findById(invalidCustomerId))
                .thenThrow(CustomerNotFoundException.class);

        String inputJson = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "phone": "1191234564",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .put("/api/v1/customers/{customerId}", invalidCustomerId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(
                        "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
                        "type", Matchers.is("/errors/not-found"),
                        "title", Matchers.is("Not found"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue()
                );
    }

    @Test
    void updateCustomerError409Contract() {
        var customerID = UUID.randomUUID();

        Mockito.doThrow(CustomerEmailIsInUseException.class)
                .when(customerManagementApplicationService)
                .update(Mockito.any(UUID.class), Mockito.any(CustomerUpdateInput.class));

        String inputJson = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1991-07-05",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .put("/api/v1/customers/{customerId}", customerID)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.CONFLICT.value())
                .body(
                        "status", Matchers.is(HttpStatus.CONFLICT.value()),
                        "type", Matchers.is("/errors/conflict"),
                        "title", Matchers.is("Conflict"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue()
                );
    }

    @Test
    void updateCustomerError422Contract() {
        var customerID = UUID.randomUUID();

        Mockito.doThrow(DomainException.class)
                .when(customerManagementApplicationService)
                .update(Mockito.any(UUID.class), Mockito.any(CustomerUpdateInput.class));

        String inputJson = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1991-07-05",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .put("/api/v1/customers/{customerId}", customerID)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body(
                        "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                        "type", Matchers.is("/errors/unprocessable-entity"),
                        "title", Matchers.is("Unprocessable Entity"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue()
                );
    }

    @Test
    void deleteCustomerError404Contract() {
        var invalidCustomerId = UUID.randomUUID();

        Mockito.doThrow(CustomerNotFoundException.class).
                when(customerManagementApplicationService)
                .archive(Mockito.any(UUID.class));

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/v1/customers/{customerId}", invalidCustomerId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(
                        "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
                        "type", Matchers.is("/errors/not-found"),
                        "title", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue()
                );
    }

    @Test
    void deleteCustomerError422Contract() {
        var invalidCustomerId = UUID.randomUUID();

        Mockito.doThrow(DomainException.class).
                when(customerManagementApplicationService)
                .archive(Mockito.any(UUID.class));

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/v1/customers/{customerId}", invalidCustomerId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body(
                        "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                        "type", Matchers.is("/errors/unprocessable-entity"),
                        "title", Matchers.is("Unprocessable Entity"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue()
                );
    }

    @Test
    void deleteCustomerError500Contract() {
        var invalidCustomerId = UUID.randomUUID();

        Mockito.doThrow(RuntimeException.class).
                when(customerManagementApplicationService)
                .archive(Mockito.any(UUID.class));

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/v1/customers/{customerId}", invalidCustomerId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(
                        "status", Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "type", Matchers.is("/errors/internal"),
                        "title", Matchers.is("Internal Server Error"),
                        "instance", Matchers.notNullValue(),
                        "detail", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue()
                );
    }


}
