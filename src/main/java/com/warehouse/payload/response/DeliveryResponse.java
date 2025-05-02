package com.warehouse.payload.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DeliveryResponse {
    private Long id;
    private Long orderId;
    private LocalDate scheduledDate;
    private String deliveryAddress;
    private String deliveryNotes;
    private String status;

    public void setDeliveryDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
