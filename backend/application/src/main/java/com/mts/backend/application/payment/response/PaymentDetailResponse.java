package com.mts.backend.application.payment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDetailResponse {
    private Long id;
    private PaymentMethodDetailResponse paymentMethod; 
    private BigDecimal amountPaid;
    private BigDecimal change;
    private Long orderId;
    private LocalTime paymentTime;
    private String status;
}
