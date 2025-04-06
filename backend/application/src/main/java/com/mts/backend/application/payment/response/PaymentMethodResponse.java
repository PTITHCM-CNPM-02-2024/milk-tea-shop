package com.mts.backend.application.payment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentMethodResponse {
    private Integer id;
    private String name;    
    private String description;
}
