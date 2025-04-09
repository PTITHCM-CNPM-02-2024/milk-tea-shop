package com.mts.backend.api.store.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AreaBaseRequest {
    private Integer id;
    private String name;
    private Boolean isActive;
    private Integer maxTable;
    private String description;
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public Optional<Integer> getMaxTable() {
        return Optional.ofNullable(maxTable);
    }
}
