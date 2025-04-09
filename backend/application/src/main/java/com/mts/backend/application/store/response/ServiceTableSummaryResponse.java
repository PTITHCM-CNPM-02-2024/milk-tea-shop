package com.mts.backend.application.store.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ServiceTableSummaryResponse {
    private Integer id;
    private String name;
    private Boolean isActive;
    private Integer areaId;
}
