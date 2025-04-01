package com.mts.backend.application.order.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderDiscountDetailResponse {
    private String name;
    private String couponCode;
    private String discountValue;
    private BigDecimal discountAmount;
}
