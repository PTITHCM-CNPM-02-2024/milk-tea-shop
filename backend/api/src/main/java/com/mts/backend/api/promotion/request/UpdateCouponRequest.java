package com.mts.backend.api.promotion.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = false)   
public class UpdateCouponRequest extends CouponBaseRequest{
}
