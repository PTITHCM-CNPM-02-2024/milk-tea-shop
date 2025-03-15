package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductSizeRequest {
    private String name;
    private Integer unitId;
    private String description;
    private Integer quantity;
}
