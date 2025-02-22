package com.shopify.order.service.impl;

import com.shopify.order.dto.OrderLineItemsDTO;
import com.shopify.order.dto.OrderRequest;
import com.shopify.order.entity.Order;
import com.shopify.order.entity.OrderLineItems;
import com.shopify.order.repository.OrderRepository;
import com.shopify.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDTOList().stream()
                .map(this::mapToOrderLineItems)
                .toList();

        order.setOrderLineItemsList(orderLineItemsList);
        orderRepository.save(order);
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
