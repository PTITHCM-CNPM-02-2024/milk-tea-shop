package com.mts.backend.application.store.query;

import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ServiceTableByIdQuery implements IQuery<CommandResult> {
    private ServiceTableId id;
}
