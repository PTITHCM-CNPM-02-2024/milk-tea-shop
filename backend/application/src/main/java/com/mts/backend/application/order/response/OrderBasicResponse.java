package com.mts.backend.application.order.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class OrderBasicResponse {
    
    private Long orderId;
    private Long customerId;
    private Long employeeId;
    private String orderStatus;
    private Instant orderTime;
    private List<OrderProductResponse> orderProducts;
    private List<OrderTableResponse> orderTables;
    private List<OrderDiscountResponse> orderDiscounts;
    private String note;
    private BigDecimal totalAmount;
    private BigDecimal finalAmount;
    
}
