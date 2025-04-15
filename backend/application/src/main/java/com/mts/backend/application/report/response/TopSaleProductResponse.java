package com.mts.backend.application.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class TopSaleProductResponse {
    private Integer productId;
    private String productName;
    private String categoryName;
    private Long quantity;
    private BigDecimal revenue;
}
