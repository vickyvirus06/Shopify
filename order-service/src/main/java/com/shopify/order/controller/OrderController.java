package com.shopify.order.controller;

import com.shopify.order.dto.OrderRequest;
import com.shopify.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @CircuitBreaker(name="inventory", fallbackMethod = "inventoryFallBackMethod")
    @TimeLimiter(name="inventory")
    public CompletableFuture<ResponseEntity<String>> placeOrder(@RequestBody OrderRequest orderRequest) {
        String message = orderService.placeOrder(orderRequest);
        return CompletableFuture.supplyAsync(() ->  ResponseEntity.status(HttpStatus.CREATED).body("Order Successfully created"));
    }

    public CompletableFuture<ResponseEntity<String>> inventoryFallBackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                "Inventory Service is down or not responding please try again later"));
    }
}
