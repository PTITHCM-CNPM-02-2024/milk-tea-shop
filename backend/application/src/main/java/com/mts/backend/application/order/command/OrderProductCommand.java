package com.mts.backend.application.order.command;

import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class OrderProductCommand {
    private ProductId productId;
    private ProductSizeId sizeId;
    @EqualsAndHashCode.Exclude
    private Integer quantity;
    private String option;
}
