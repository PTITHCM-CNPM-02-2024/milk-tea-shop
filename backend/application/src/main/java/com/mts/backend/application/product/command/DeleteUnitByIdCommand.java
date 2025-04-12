package com.mts.backend.application.product.command;

import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@Data
@AllArgsConstructor
public class DeleteUnitByIdCommand implements ICommand<CommandResult> {
    private UnitOfMeasureId id;
}
