package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {
    String name;
    String description;
    Integer parentId;
}
