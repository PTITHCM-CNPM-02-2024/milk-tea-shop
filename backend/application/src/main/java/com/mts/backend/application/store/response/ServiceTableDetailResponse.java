package com.mts.backend.application.store.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ServiceTableDetailResponse {
    private Integer id;
    private String name;
    private AreaDetailResponse area;
    private Boolean isActive;
}
