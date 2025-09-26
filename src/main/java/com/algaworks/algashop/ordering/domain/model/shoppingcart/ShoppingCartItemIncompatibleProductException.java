package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.exception.DomainException;

public class ShoppingCartItemIncompatibleProductException extends DomainException {

  public ShoppingCartItemIncompatibleProductException(String message) {
    super(message);
  }

}
