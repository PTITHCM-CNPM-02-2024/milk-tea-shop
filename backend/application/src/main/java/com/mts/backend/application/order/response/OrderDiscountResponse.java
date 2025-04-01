package com.mts.backend.application.order.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class OrderDiscountResponse {
    private Long orderDiscountId;
    private Long discountId;
}
