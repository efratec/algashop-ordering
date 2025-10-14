package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.entity.fixture.CustomerTestFixture;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static com.algaworks.algashop.ordering.application.checkout.BuyNowInputTestFixture.aBuyNowInput;
import static com.algaworks.algashop.ordering.domain.model.entity.fixture.ProductTestFixture.aProduct;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BuyNowApplicationServiceTestIT {

    private final BuyNowApplicationService buyNowApplicationService;
    private final Orders orders;
    private final Customers customers;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoBean
    private ShippingCostService shippingCostService;

    @BeforeEach
    public void setup() {
        if (!customers.exists(CustomerTestFixture.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestFixture.existingCustomer().build());
        }
    }

    @Test
    public void shouldBuyNow() {
        final var product = aProduct().build();
        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));

        when(shippingCostService.calculate(Mockito.any(ShippingCostService.CalculationRequest.class)))
                .thenReturn(new ShippingCostService.CalculationResult(
                        Money.of("10.00"),
                        LocalDate.now().plusDays(3)
                ));

        var input = aBuyNowInput().build();

        var orderId = buyNowApplicationService.buyNow(input);

        Assertions.assertThat(orderId).isNotBlank();
        Assertions.assertThat(orders.exists(OrderId.from(orderId))).isTrue();
    }

}
