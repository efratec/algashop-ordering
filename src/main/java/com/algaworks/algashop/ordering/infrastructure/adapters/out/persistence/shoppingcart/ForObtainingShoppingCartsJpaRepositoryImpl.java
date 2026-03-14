package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.core.ports.out.shoppingcart.ForObtainingShoppingCarts;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ForObtainingShoppingCartsJpaRepositoryImpl implements ForObtainingShoppingCarts {

    private final ShoppingCartPersistenceEntityRepository repository;
    private final ShoppingCartPersistenceEntityDisassembler disassembler;

    @Override
    public ShoppingCartOutput findById(UUID shoppingCartId) {
        ShoppingCartPersistenceEntity entity = repository.findById(shoppingCartId)
                .orElseThrow(() -> ShoppingCartNotFoundException.because(shoppingCartId));
        return disassembler.toCartOutput(entity);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(UUID customerId) {
        ShoppingCartPersistenceEntity entity = repository.findByCustomer_Id(customerId)
                .orElseThrow(() -> ShoppingCartNotFoundException.because(customerId));
        return disassembler.toCartOutput(entity);
    }

}
