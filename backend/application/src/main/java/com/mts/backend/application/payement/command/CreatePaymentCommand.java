package com.mts.backend.application.payement.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreatePaymentCommand {
    private Long orderId;
    private Integer paymentMethod;
    private BigDecimal amountPaid;
}
