package com.mts.backend.application.payment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentMethodDetailResponse {
    private Integer id;
    private String name;    
    private String description;
}
