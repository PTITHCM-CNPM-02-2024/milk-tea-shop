package com.mts.backend.application.product.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UnitDetailResponse {
    private Integer id;
    private String name;
    private String symbol;
    private String description;
    
}
