package com.mts.backend.application.store.query;

import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AreaByIdQuery implements IQuery<CommandResult> {
    
    private AreaId id;
}
