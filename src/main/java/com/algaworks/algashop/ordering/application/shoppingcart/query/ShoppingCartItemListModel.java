package com.algaworks.algashop.ordering.application.shoppingcart.query;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ShoppingCartItemListModel {

    private List<ShoppingCartItemOutput> items;

}
