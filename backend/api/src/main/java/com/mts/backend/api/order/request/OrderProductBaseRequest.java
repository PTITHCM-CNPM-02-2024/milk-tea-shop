package com.mts.backend.api.order.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class OrderProductBaseRequest {
    private Integer productId;
    private Integer sizeId;
    private Integer quantity;
    private String option;
    
    public Optional<String> getOption() {
        return Optional.ofNullable(option);
    }
}
