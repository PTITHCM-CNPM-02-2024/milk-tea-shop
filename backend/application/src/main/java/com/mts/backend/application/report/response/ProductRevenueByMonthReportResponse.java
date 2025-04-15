package com.mts.backend.application.report.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ProductRevenueByMonthReportResponse extends TopSaleProductResponse {
}
