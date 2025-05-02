package com.warehouse.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderApprovalRequest {
    @NotNull
    private Boolean approved;

    private String declineReason;
}