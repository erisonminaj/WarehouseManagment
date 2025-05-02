package com.warehouse.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryRequest {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @Min(value = 0, message = "Stock quantity must be zero or positive")
    private int stockQuantity;
}
