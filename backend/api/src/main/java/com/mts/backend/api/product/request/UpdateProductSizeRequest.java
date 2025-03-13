package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateProductSizeRequest {
    private Integer id;
    private Integer unitId;
    private String name;
    private String description;
    private Integer quantity;
}
