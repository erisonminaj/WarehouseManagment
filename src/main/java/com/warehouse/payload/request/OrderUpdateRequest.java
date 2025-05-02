package com.warehouse.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateRequest {
    private List<OrderItemRequest> items;
}