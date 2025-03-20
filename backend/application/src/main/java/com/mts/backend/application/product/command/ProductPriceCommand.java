package com.mts.backend.application.product.command;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class ProductPriceCommand {
    Integer sizeId;
    @EqualsAndHashCode.Exclude
    BigDecimal price;
}
