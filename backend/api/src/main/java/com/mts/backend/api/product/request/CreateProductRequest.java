package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DTO for {@link com.mts.backend.domain.product.Product}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    String name;
    String description;
    Integer categoryId;
    boolean available;
    boolean signature;
    String imagePath;
    
    Map<Integer, BigDecimal> prices = new HashMap<>();
}