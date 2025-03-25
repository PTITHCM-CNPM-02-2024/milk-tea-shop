package com.mts.backend.application.order.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ApplyDiscountToOrderCommand {
    private Long orderId;
    private Long discountId;
}
