package com.shopify.inventory.service;

import com.shopify.inventory.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {

    public List<InventoryResponse> isInStock(List<String> skuCodes);
}
