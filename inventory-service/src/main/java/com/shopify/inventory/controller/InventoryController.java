package com.shopify.inventory.controller;

import com.shopify.inventory.dto.InventoryResponse;
import com.shopify.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public List<InventoryResponse> isInStock(@RequestParam("skuCodes") List<String> skuCodes) {
        return inventoryService.isInStock(skuCodes);
    }
}
