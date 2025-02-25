package com.shopify.inventory.service.impl;

import com.shopify.inventory.Entity.Inventory;
import com.shopify.inventory.dto.InventoryResponse;
import com.shopify.inventory.repository.InventoryRepository;
import com.shopify.inventory.service.InventoryService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        List<InventoryResponse> inventoryResponses = new ArrayList<>();

        for (String skuCode : skuCodes) {
            Inventory inventory = inventoryRepository.findBySkuCode(skuCode).orElse(null);
            InventoryResponse inventoryResponse = new InventoryResponse();
            if (inventory != null) {
                inventoryResponse.setInStock(inventory.getQuantity() > 0);
            } else {
                inventoryResponse.setInStock(false);
            }
            inventoryResponse.setSkuCode(skuCode);

            inventoryResponses.add(inventoryResponse);
        }
        return inventoryResponses;
    }
}
