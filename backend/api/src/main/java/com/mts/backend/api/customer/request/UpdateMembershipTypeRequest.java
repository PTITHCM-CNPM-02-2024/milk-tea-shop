package com.mts.backend.api.customer.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateMembershipTypeRequest {
    private Integer id;
    private String name;
    private String description;
    private String validUntil;
    private Boolean active;
    private Integer requiredPoint;
    private String discountUnit;
    private Double discountValue;
}
