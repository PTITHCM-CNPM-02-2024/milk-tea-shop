package com.mts.backend.application.payment.response;

import com.mts.backend.domain.payment.identifier.PaymentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentResult {
    private PaymentId paymentId;
    private Long transactionId;
}
