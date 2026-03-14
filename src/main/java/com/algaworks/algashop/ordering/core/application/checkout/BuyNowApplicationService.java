package com.algaworks.algashop.ordering.core.application.checkout;

import com.algaworks.algashop.ordering.core.application.order.BillingInputDisassembler;
import com.algaworks.algashop.ordering.core.application.order.ShippingInputDisassembler;
import com.algaworks.algashop.ordering.core.domain.exception.DomainEntityNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.order.*;
import com.algaworks.algashop.ordering.core.domain.model.product.Product;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.core.ports.in.checkout.BuyNowInput;
import com.algaworks.algashop.ordering.core.ports.in.checkout.ForBuyingProduct;
import com.algaworks.algashop.ordering.core.ports.in.order.ShippingInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BuyNowApplicationService implements ForBuyingProduct {

    private final BuyNowService buyNowService;
    private final ProductCatalogService productCatalogService;

    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;

    private final Orders orders;
    private final Customers customers;

    private final ShippingInputDisassembler shippingInputDisassembler;
    private final BillingInputDisassembler billingInputDisassembler;


    @Transactional
    @Override
    public String buyNow(BuyNowInput input) {
        Objects.requireNonNull(input);

        var paymentMethod = PaymentMethodEnum.valueOf(input.getPaymentMethod());
        var customerId = CustomerId.from(input.getCustomerId());
        var quantity = Quantity.of(input.getQuantity());
        var productId = ProductId.from(input.getProductId());

        CreditCardId creditCardId = null;

        if (PaymentMethodEnum.CREDIT_CARD.equals(paymentMethod)) {
            if (input.getCreditCardId() == null) {
                throw new DomainEntityNotFoundException("Credit card id is already set");
            }
            creditCardId = new CreditCardId(input.getCreditCardId());
        }

        var product = findProduct(productId);

        var customer = customers.ofId(customerId).orElseThrow(() -> CustomerNotFoundException.because(customerId.value()));

        var shippingCalculatinoResult = calculateShippingCost(input.getShipping());

        var shipping = shippingInputDisassembler.toDomainModel(input.getShipping(), shippingCalculatinoResult);
        var billing = billingInputDisassembler.toDomainModel(input.getBilling());

        var order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod, creditCardId);

        orders.add(order);

        return order.id().value().toString();
    }

    private ShippingCostService.CalculationResult calculateShippingCost(ShippingInput shipping) {
        var origin = originAddressService.originAddress().zipCode();
        var destination = ZipCode.of(shipping.getAddress().getZipCode());
        return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
    }

    private Product findProduct(ProductId productId) {
        return productCatalogService.ofId(productId)
                .orElseThrow(() -> ProductNotFoundException.because(productId.value()));
    }

}
