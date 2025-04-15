package com.mts.backend.application.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class CatRevenueByTimeResponse extends CatRevenueBasicResponse{
    private String name;
    private BigDecimal revenue;
}
