package com.warehouse.payload.response;

import com.warehouse.model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSummaryResponse {
    private Long id;
    private String clientName;
    private OrderStatus status;
    private LocalDateTime submittedAt;
    private Integer itemCount;
    private Double totalAmount;
}