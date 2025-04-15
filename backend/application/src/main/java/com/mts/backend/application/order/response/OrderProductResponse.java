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
    private Integer prodId;
    private Integer sizeId;
    private Integer quantity;
    private String option;
    
}
