package com.mts.backend.application.account.query;

import com.mts.backend.domain.account.identifier.RoleId;
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
public class RoleByIdQuery implements IQuery<CommandResult> {
    
    private RoleId id;
}
