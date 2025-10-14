package com.algaworks.algashop.ordering.application.checkout;


import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService.CalculationRequest;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService {

    private final ShoppingCarts shoppingCarts;
    private final ShippingCostService costService;
    private final OriginAddressService originAddressService;
    private final CheckoutService checkoutService;
    private final Orders orders;

    private final BillingInputDisassembler billingInputDisassembler;
    private final ShippingInputDisassembler shippingInputDisassembler;

    @Transactional
    public String checkout(CheckoutInput input) {
        requireNonNull(input);

        var paymentMethod = PaymentMethodEnum.valueOf(input.getPaymentMethod());

        var shoppingCart = shoppingCarts.ofId(ShoppingCartId.from(input.getShoppingCartId()))
                .orElseThrow(() -> ShoppingCartNotFoundException.because(input.getShoppingCartId()));

        var originAddress = originAddressService.originAddress().zipCode();
        var destinationAddress = ZipCode.of(input.getShipping().getAddress().getZipCode());

        var shippingCalculation = costService.calculate(
                CalculationRequest
                        .builder()
                        .origin(originAddress)
                        .destination(destinationAddress)
                        .build());

        var shipping = shippingInputDisassembler.toDomainModel(input.getShipping(), shippingCalculation);
        var billing = billingInputDisassembler.toDomainModel(input.getBilling());

        var orderCheckout = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

        orders.add(orderCheckout);
        shoppingCarts.add(shoppingCart);

        return orderCheckout.id().value().toString();
    }

}
