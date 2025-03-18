package com.mts.backend.api.customer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateMembershipTypeRequest {
    private String name;
    private String description;
    private int requiredPoint;
    private String discountUnit;
    private double discountValue;
    private String validUntil;
}
