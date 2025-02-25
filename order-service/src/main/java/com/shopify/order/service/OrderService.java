package com.shopify.order.service;

import com.shopify.order.dto.OrderRequest;

public interface OrderService {

    public String placeOrder(OrderRequest orderRequest);
}
