package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductSizeBaseRequest {
    private Integer id;
    private String name;
    private Integer unitId;
    private String description;
    private Integer quantity;
}
