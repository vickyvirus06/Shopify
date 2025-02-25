package com.shopify.order.service.impl;

import com.shopify.order.dto.InventoryResponse;
import com.shopify.order.dto.OrderLineItemsDTO;
import com.shopify.order.dto.OrderRequest;
import com.shopify.order.entity.Order;
import com.shopify.order.entity.OrderLineItems;
import com.shopify.order.event.OrderNotificationEvent;
import com.shopify.order.repository.OrderRepository;
import com.shopify.order.service.OrderService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private KafkaTemplate<String, OrderNotificationEvent> kafkaTemplate;

    @Override
    @Retry(name="inventory")
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDTOList().stream()
                .map(this::mapToOrderLineItems)
                .toList();

        order.setOrderLineItemsList(orderLineItemsList);

        List<String> skuCodes = orderLineItemsList.stream().map(OrderLineItems::getSkuCode).toList();

        InventoryResponse[] inventoryResponseList = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder ->
                                uriBuilder.queryParam("skuCodes", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class).block();

        assert inventoryResponseList != null;
        List<String> productsNotInStock = Arrays.stream(inventoryResponseList).filter(inventory -> !inventory.isInStock())
                .map(InventoryResponse::getSkuCode).toList();

        if (productsNotInStock.isEmpty()) {
            orderRepository.save(order);
            List<OrderNotificationEvent> orderNotificationEvents = orderLineItemsList.stream().map(this::mapToOrderEventNotification).toList();
            orderNotificationEvents.forEach(orderNotificationEvent ->
                    kafkaTemplate.send("topic-order-place-event", orderNotificationEvent));

            return "Order Successfully created";
        } else {
            log.error("Product is not in stock {}", productsNotInStock);
            return "Product is not in stock " + productsNotInStock.toString();
        }
    }

    private OrderNotificationEvent mapToOrderEventNotification(OrderLineItems orderLineItem) {

        return OrderNotificationEvent.builder().id(orderLineItem.getId())
                .skuCode(orderLineItem.getSkuCode())
                .price(orderLineItem.getPrice())
                .quantity(orderLineItem.getQuantity())
                .build();
    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsDTO orderLineItemsDTO) {

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(orderLineItems.getId());
        orderLineItems.setSkuCode(orderLineItemsDTO.getSkuCode());
        orderLineItems.setQuantity(orderLineItems.getQuantity());
        orderLineItems.setPrice(orderLineItemsDTO.getPrice());

        return orderLineItems;
    }
}
