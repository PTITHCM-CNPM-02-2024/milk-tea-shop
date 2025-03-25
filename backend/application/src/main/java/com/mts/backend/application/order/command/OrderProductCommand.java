package com.mts.backend.application.order.command;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class OrderProductCommand {
    private Integer productId;
    private Integer sizeId;
    @EqualsAndHashCode.Exclude
    private Integer quantity;
    private String option;
}
