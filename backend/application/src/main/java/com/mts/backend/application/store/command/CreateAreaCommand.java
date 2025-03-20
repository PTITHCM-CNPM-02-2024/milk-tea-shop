package com.mts.backend.application.store.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateAreaCommand  implements ICommand<CommandResult> {
    private String name;
    private String description;
    private Integer maxTable;
    private Boolean isActive;
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public Optional<Integer> getMaxTable() {
        return Optional.ofNullable(maxTable);
    }
}
