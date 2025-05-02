package com.warehouse.payload.response;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long id;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private Double priceAtOrder;
    private Double totalPrice;
}