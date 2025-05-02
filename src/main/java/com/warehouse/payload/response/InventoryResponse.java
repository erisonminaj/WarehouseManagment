package com.warehouse.payload.response;

import lombok.Data;

@Data
public class InventoryResponse {

    private Long inventoryId;
    private Long itemId;
    private String itemName;
    private String itemDescription;
    private double itemPrice;
    private int stockQuantity;
}
