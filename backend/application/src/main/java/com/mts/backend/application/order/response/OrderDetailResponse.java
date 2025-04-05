package com.mts.backend.application.order.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderDetailResponse {
    private Long orderId;
    private Long customerId;
    private String customerName;
    private Long employeeId;
    private String employeeName;
    private String employeePhone;
    private String orderStatus;
    private Instant orderTime;
    private BigDecimal totalAmount;
    private BigDecimal finalAmount;
    private BigDecimal discountAmount;
    @Builder.Default
    private List<OrderProductDetailResponse> orderProducts = new ArrayList<>();
    @Builder.Default
    private List<OrderTableDetailResponse> orderTables = new ArrayList<>();
    @Builder.Default
    private List<OrderDiscountDetailResponse> orderDiscounts = new ArrayList<>();
}
