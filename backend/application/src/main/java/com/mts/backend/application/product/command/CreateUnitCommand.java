package com.mts.backend.application.product.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateUnitCommand implements ICommand<CommandResult> {
    private String name;
    private String symbol;
    private String description;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
