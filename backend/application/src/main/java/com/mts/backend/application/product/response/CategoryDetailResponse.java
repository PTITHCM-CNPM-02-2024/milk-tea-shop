package com.mts.backend.application.product.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class CategoryDetailResponse extends CategorySummaryResponse {
    private List<ProductSummaryResponse> products;
}
