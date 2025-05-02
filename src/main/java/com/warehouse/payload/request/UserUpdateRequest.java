package com.warehouse.payload.request;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Boolean enabled;
}
