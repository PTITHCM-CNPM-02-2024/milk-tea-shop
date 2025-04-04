package com.mts.backend.api.store.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AreaBaseRequest {
    private Integer id;
    private String name;
    private Boolean isActive;
    private Integer maxTable;
}
