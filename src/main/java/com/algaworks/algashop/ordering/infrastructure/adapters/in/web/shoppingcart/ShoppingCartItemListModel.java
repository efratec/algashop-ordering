package com.algaworks.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemOutput;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ShoppingCartItemListModel {

    private List<ShoppingCartItemOutput> items;

}
