package com.algaworks.algashop.ordering.contract.base;

import com.algaworks.algashop.ordering.application.checkout.BuyNowApplicationService;
import com.algaworks.algashop.ordering.application.checkout.BuyNowInput;
import com.algaworks.algashop.ordering.application.checkout.CheckoutApplicationService;
import com.algaworks.algashop.ordering.application.checkout.CheckoutInput;
import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutputTestFixture;
import com.algaworks.algashop.ordering.application.order.query.OrderFilter;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.application.order.query.OrderSummaryOutputTestFixture;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.presentation.order.OrderController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = OrderController.class)
public class OrderBase {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private OrderQueryService orderQueryService;

    @MockitoBean
    private BuyNowApplicationService buyNowApplicationService;

    @MockitoBean
    private CheckoutApplicationService checkoutApplicationService;

    private static final String validOrderId = "01226N0640J7Q";
    private static final String notFoundOrderId = "01226N0693HDH";

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context)
                        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                        .build()
        );

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        when(buyNowApplicationService.buyNow(any(BuyNowInput.class)))
                .thenReturn(validOrderId);

        when(orderQueryService.findById(validOrderId))
                .thenReturn(OrderDetailOutputTestFixture.placedOrder(validOrderId).build());

        when(orderQueryService.findById(notFoundOrderId))
                .thenThrow(new OrderNotFoundException());

        when(orderQueryService.filter(any(OrderFilter.class)))
                .thenReturn(new PageImpl<>(
                        List.of(OrderSummaryOutputTestFixture.placedOrder().id(validOrderId).build())
                ));

        when(checkoutApplicationService.checkout(any(CheckoutInput.class))).thenReturn(validOrderId);
    }

}
