package com.mts.backend.application.store.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class ServiceTableDetailResponse extends ServiceTableSummaryResponse {
    private AreaSummaryResponse area;
}
