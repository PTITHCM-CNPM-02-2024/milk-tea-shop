package com.mts.backend.application.product.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Data
@SuperBuilder
public class SizeSummaryResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer quantity;
    private Integer unitId;
}
