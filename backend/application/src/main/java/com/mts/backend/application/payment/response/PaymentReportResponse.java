package com.mts.backend.application.payment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentReportResponse {
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;
    private Long totalPayment;
    private List<PaymentDetailResponse> paymentDetailResponses;
}
