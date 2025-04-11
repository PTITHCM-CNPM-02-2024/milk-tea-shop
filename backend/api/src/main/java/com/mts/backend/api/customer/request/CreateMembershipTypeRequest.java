package com.mts.backend.api.customer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateMembershipTypeRequest {
    private String name;
    private String description;
    private Integer requiredPoint;
    private String discountUnit;
    private BigDecimal discountValue;
    private String validUntil;
}
