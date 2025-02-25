package com.shopify.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderNotificationEvent {

    private Long id;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
