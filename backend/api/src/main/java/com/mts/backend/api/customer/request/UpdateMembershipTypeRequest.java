package com.mts.backend.api.customer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateMembershipTypeRequest {
    private Integer id;
    private String name;
    private String description;
    private String validUntil;
    private boolean isActive;
    private int requiredPoint;
    private String discountUnit;
    private double discountValue;
}
