package com.mts.backend.application.store.query;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@Builder
@Data
@AllArgsConstructor
public class ServiceTableActiveQuery implements IQuery<CommandResult> {
    private Boolean active;
}
