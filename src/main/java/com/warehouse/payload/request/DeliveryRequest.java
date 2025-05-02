package com.warehouse.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DeliveryRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private LocalDate deliveryDate;

    private String deliveryAddress;

    private String deliveryNotes;

    private List<Long> truckIds;
}
