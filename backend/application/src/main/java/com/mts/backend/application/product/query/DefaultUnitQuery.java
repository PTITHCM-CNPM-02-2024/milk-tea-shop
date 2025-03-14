package com.mts.backend.application.product.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class DefaultUnitQuery implements  IQuery<CommandResult> {
    
}
