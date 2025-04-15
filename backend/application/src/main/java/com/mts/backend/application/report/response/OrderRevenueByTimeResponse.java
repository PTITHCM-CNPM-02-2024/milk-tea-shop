package com.mts.backend.application.report.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderRevenueByTimeResponse {
    private String date;
    private BigDecimal revenue;
}
