package com.mts.backend.application.order.command;

import com.mts.backend.domain.promotion.identifier.DiscountId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderDiscountCommand {
    private DiscountId discountId;
}
