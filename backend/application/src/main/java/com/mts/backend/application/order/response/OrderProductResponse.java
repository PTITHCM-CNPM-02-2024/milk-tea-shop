package com.mts.backend.application.order.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class OrderProductResponse {
    private Long orderProductId;
    private Long prodId;
    private Long priceId;
    private Integer quantity;
    private String option;
    
}
