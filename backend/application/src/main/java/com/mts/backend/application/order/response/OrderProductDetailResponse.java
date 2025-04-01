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
public class OrderProductDetailResponse {
    private String productName;
    private String productOption;
    private Integer quantity;
    private BigDecimal price;
    private String size;
    private BigDecimal totalPrice;
}
