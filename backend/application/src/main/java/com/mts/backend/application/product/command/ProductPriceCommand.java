package com.mts.backend.application.product.command;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductPriceCommand {
    ProductSizeId sizeId;
    Money price;
}
