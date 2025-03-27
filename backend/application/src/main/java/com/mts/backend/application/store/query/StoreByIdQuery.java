package com.mts.backend.application.store.query;

import com.mts.backend.domain.store.identifier.StoreId;
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
public class StoreByIdQuery implements IQuery<CommandResult> {
    private StoreId id;
}
