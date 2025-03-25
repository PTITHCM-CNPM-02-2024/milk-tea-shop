package com.mts.backend.application.payment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDetailResponse {
    private String paymentMethod;
    private BigDecimal amountPaid;
    private BigDecimal change;
    
}
