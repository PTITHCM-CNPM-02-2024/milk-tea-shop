package com.mts.backend.application.store.query;

import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ServiceTableByAreaIdQuery implements IQuery<CommandResult> {
    private AreaId areaId;
    private Integer page;
    private Integer size;
    
    public Optional<AreaId> getAreaId() {
        return Optional.ofNullable(areaId);
    }
}
