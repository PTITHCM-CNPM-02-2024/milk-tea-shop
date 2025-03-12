package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductInformRequest {
    private String name;
    private String description;
    private Integer categoryId;
    private boolean isAvailable;
    private boolean isSignature;
}
