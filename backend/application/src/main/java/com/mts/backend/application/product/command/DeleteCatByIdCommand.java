package com.mts.backend.application.product.command;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DeleteCatByIdCommand implements ICommand<CommandResult> {
    private CategoryId id;
}
