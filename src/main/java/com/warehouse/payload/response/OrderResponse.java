package com.warehouse.payload.response;

import com.warehouse.model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime lastUpdatedAt;
    private String declineReason;
    private List<OrderItemResponse> items;
    private Double totalAmount;
    private DeliveryResponse delivery;
}