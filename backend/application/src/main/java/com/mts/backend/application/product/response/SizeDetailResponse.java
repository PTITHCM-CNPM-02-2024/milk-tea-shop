package com.mts.backend.application.product.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class SizeDetailResponse extends SizeSummaryResponse {
    private UnitDetailResponse unit;
}
