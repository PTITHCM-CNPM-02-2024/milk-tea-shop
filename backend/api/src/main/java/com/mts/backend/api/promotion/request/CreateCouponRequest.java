package com.mts.backend.api.promotion.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class CreateCouponRequest extends CouponBaseRequest{
}
