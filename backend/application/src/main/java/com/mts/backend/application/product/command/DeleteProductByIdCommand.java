package com.mts.backend.application.product.command;

import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DeleteProductByIdCommand implements ICommand<CommandResult> {
    private ProductId id;
}
