package com.mts.backend.api.promotion.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class UpdateDiscountRequest extends DiscountBaseRequest{
}
