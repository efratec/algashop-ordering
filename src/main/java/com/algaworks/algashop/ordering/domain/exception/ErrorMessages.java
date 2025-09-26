package com.algaworks.algashop.ordering.domain.exception;

public class ErrorMessages {

    public static final String VALIDATION_ERROR_FULLNAME_IS_NULL = "FullName cannot be null";

    public static final String ERROR_CUSTOMER_ARCHIVED = "Customer is archived it cannot be changed";

    public static final String ERROR_ORDER_STATUS_CANNOT_BE_CHANGED = "Cannot change order status %s status from %s to %s";
    public static final String ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST = "Order %s expected delivery date cannot be in the past";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS = "Order %s cannot be closed, it has no items";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO = "Order %s cannot be placed, it has no shipping info";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO = "Order %s cannot be placed, it has no billing info";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD = "Order %s cannot be placed, it has no payment method";
    public static final String ERROR_ORDER_DOES_NOT_CONTAIN_ITEM = "Order %s does not contain item %s";
    public static final String ERROR_PRODUCT_IS_OUT_OF_STOCK = "Product %s is out of stock";
    public static final String ERROR_ORDER_CANNOT_BE_EDITTED = "Order %s with status %s cannot be edited";
    public static final String ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM = "Shopping Cart %s does not contain item %s";
    public static final String ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT = "Shopping Cart %s cannot be updated, incompatible product %s";
    public static final String ERROR_SHOPPING_CART_NOT_AVAILABLE = "Shopping Cart %s is not available";
    public static final String ERROR_ORDER_NOT_BELONS_TO_CUSTOMER = "Order not belongs to customer";
    public static final String ERROR_CUSTOMER_NOT_FOUND = "Customer %s not found";
    public static final String ERROR_CUSTOMER_ALREADY_HAVE_SHOPPING_EXISTS = "Customer %s already have shopping cart exists";
    public static final String ERROR_PRODUCT_NOT_FOUND =  "Product %s not found";
    public static final String ERROR_CUSTOMER_EMAIL_IS_ALREADY_USED = "Customer email is already used";

}
