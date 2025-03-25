package com.mts.backend.api.order.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class OrderProductBaseRequest {
    private Integer productId;
    private Integer sizeId;
    private Integer quantity;
    private String option;
}
