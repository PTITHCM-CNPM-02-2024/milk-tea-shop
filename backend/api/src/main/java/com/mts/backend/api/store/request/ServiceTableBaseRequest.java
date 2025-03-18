package com.mts.backend.api.store.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServiceTableBaseRequest {
    protected Integer id;
    protected String name;
    protected String description;
    protected Integer areaId;
    protected Boolean isActive;
}
