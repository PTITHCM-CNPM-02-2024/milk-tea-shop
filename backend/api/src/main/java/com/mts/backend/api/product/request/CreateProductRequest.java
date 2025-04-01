package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    String name;
    String description;
    Integer categoryId;
    Boolean available;
    Boolean signature;
    String imagePath;
    
    Map<Integer, BigDecimal> prices = new HashMap<>();
}