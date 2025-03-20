package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class UpdateProductInformRequest extends ProductBaseRequest{
    private String name;
    private String description;
    private Integer categoryId;
    private Boolean isAvailable;
    private Boolean isSignature;
}
