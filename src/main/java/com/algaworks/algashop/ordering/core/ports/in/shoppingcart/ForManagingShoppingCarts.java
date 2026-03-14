package com.algaworks.algashop.ordering.core.ports.in.shoppingcart;

import java.util.UUID;

public interface ForManagingShoppingCarts {

    void addItem(ShoppingCartItemInput itemInput);
    UUID createNew(UUID rawCustomerId);
    void removeItem(UUID rawShoppingCartId, UUID shoppingCartItemId);
    void empty(UUID rawShoppingCartId);
    void delete(UUID rawShoppingCartId);

}
