package com.mts.backend.application.promotion.query;

import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DiscountByCouponQuery implements IQuery<CommandResult> {
    private CouponCode couponId;
}
