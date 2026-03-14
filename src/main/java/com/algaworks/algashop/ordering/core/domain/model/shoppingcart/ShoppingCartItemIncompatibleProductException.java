package com.algaworks.algashop.ordering.core.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.exception.DomainException;

public class ShoppingCartItemIncompatibleProductException extends DomainException {

  public ShoppingCartItemIncompatibleProductException(String message) {
    super(message);
  }

}
