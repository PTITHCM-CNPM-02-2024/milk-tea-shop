package com.mts.backend.application.report.response;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OverviewReportResponse {
    private Long totalProduct;
    private Long totalEmployee;
    private Long totalOrder;
    private Long totalCustomer;
    // đơn hàng có giá trị lớn nhất
    private BigDecimal maxOrderValue;
    // đơn hàng có giá trị nhỏ nhất
    private BigDecimal minOrderValue;
    // đơn hàng có giá trị trung bình
    private BigDecimal avgOrderValue;
    // tổng giá trị đơn hàng
    private BigDecimal sumOrderValue;
}
