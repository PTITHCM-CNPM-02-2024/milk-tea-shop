package com.mts.backend.application.product.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductSizeCommand implements ICommand<CommandResult> {
    private Integer id;
    private Integer unitId;
    private String name;
    private String description;
    private Integer quantity;
}
