package com.mts.backend.application.customer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MemberTypeDetailResponse {
    
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime validUntil;
    private Boolean isActive;
    private Integer requiredPoint;
    private String discountUnit;
    private BigDecimal discountValue;
}
