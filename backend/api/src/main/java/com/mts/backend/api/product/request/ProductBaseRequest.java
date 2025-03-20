package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ProductBaseRequest {
    private Integer id;
    private   String name;
    private  String description;
    private  Integer categoryId;
    private   Boolean available;
    private   Boolean signature;
    private   String imagePath;
    @Builder.Default
    private  Map<Integer, BigDecimal> prices = new HashMap<>();
}
