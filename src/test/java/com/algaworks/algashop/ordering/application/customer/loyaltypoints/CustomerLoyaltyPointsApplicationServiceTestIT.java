package com.algaworks.algashop.ordering.application.customer.loyaltypoints;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture.brandNewCustomer;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.OrderTestFixture.anOrder;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProduct;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomerLoyaltyPointsApplicationServiceTestIT {

    private final CustomerLoyaltyPointsApplicationService loyaltyPointsApplicationService;
    private final Orders orders;
    private final Customers customers;

    @MockitoBean
    private CustomerEventListener customerEventListener;

    @Test
    void shouldAddLoyaltyPointsToCustomer_whenOrderIsValidAndReady() {
        var customer = brandNewCustomer().build();
        customers.add(customer);
        assertThat(customer.id()).isNotNull();

        var order = anOrder()
                .customerId((customer.id()))
                .status(DRAFT)
                .withItems(false)
                .build();

        var product = aProduct()
                .price(Money.of("2000"))
                .build();

        order.addItem(product, Quantity.of(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        orders.add(order);
        assertThat(order.id()).isNotNull();

        loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().value().toString());

        var customerWithLoyaltyPointAdded = customers.ofId(customer.id()).orElseThrow();
        assertThat(customerWithLoyaltyPointAdded.id()).isNotNull();
        assertThat(customerWithLoyaltyPointAdded.loyaltyPoints()).isEqualTo(LoyaltyPoints.of(10));
    }

    @Test
    void shouldThrowException_whenAddingPointsToNonexistentCustomer() {
        var customerIdNotExisting = UUID.randomUUID();

        var newCustomer = brandNewCustomer().build();
        customers.add(newCustomer);
        assertThat(newCustomer.id()).isNotNull();

        var order = anOrder()
                .customerId(newCustomer.id())
                .status(READY)
                .build();

        orders.add(order);
        assertThat(order.id()).isNotNull();

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(customerIdNotExisting,
                        order.id().toString()));
    }

    @Test
    void shouldThrowException_when_AddingPointsToNoneExistingOrder() {
        var orderIdNotExisting = TSID.fast();

        var customer = brandNewCustomer().build();
        customers.add(customer);
        assertThat(customer.id()).isNotNull();

        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(),
                        orderIdNotExisting.toString()));
    }

    @Test
    void shouldThrowException_whenAddingPointsToArchivedCustomer() {
        var customer = brandNewCustomer().build();
        customers.add(customer);
        assertThat(customer.id()).isNotNull();

        var order = anOrder()
                .customerId(customer.id())
                .status(READY)
                .build();
        orders.add(order);
        assertThat(order.id()).isNotNull();

        customer.archive();
        customers.add(customer);
        assertThat(customer.isArchived()).isTrue();

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(),
                        order.id().value().toString()));
    }

    @Test
    void shouldThrowException_whenAddingPointsWithOrderNotBelongingToCustomer() {
        var customerA = brandNewCustomer()
                .email(Email.of("customerA@gmail.com"))
                .fullName(FullName.of("Customer A", "da Silva"))
                .build();

        var customerB = brandNewCustomer()
                .email(Email.of("customerB@gmail.com"))
                .fullName(FullName.of("Customer B", "da Silva"))
                .build();

        customers.add(customerA);
        customers.add(customerB);
        assertThat(customerA.id()).isNotNull();
        assertThat(customerB.id()).isNotNull();

        var order = anOrder()
                .customerId(customerB.id())
                .status(READY)
                .build();
        orders.add(order);
        assertThat(order.id()).isNotNull();

        assertThatExceptionOfType(OrderNotBelongsToCustomerException.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(customerA.id().value(),
                        order.id().value().toString()));
    }

    @Test
    void shouldThrowException_whenAddingPointsWithOrderNotReady() {
        var customer = brandNewCustomer().build();
        customers.add(customer);
        assertThat(customer.id()).isNotNull();

        var order = anOrder()
                .customerId(customer.id())
                .status(PLACED)
                .build();
        orders.add(order);
        assertThat(order.id()).isNotNull();

        assertThatExceptionOfType(CantAddLoyaltyPointsOrderIsNotReady.class)
                .isThrownBy(() -> loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(),
                        order.id().value().toString()));
    }

    @Test
    void shouldKeepPointsZero_whenOrderValueIsLessThanMinimum() {
        var customer = brandNewCustomer().build();
        customers.add(customer);
        assertThat(customer.id()).isNotNull();

        var order = anOrder()
                .customerId((customer.id()))
                .status(DRAFT)
                .withItems(false)
                .build();

        var product = aProduct()
                .price(Money.of("50"))
                .build();

        order.addItem(product, Quantity.of(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        orders.add(order);
        assertThat(order.id()).isNotNull();

        loyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().value().toString());

        var customerUpdated = customers.ofId(customer.id()).orElseThrow();
        assertThat(customerUpdated.id()).isNotNull();
        assertThat(customerUpdated.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    }


}