package com.mts.backend.api.payment.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreatePaymentRequest {
    private Long orderId;
    private Integer paymentMethodId;
}
