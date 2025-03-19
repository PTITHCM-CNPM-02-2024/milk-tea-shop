package com.mts.backend.application.promotion.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CouponDetailResponse {
    private Long id;
    private String coupon;
    private String description;
}
