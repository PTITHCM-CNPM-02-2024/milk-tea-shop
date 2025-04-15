package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductInformRequest {
    private String name;
    private String description;
    private Integer categoryId;
    private Boolean available;
    private Boolean signature;
    private String imagePath;
}
