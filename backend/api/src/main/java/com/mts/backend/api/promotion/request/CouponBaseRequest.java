package com.mts.backend.api.promotion.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class CouponBaseRequest {
    private Long id;
    private String coupon;
    private String description;
}
