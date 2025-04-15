package com.mts.backend.application.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class CatRevenueBasicResponse {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
