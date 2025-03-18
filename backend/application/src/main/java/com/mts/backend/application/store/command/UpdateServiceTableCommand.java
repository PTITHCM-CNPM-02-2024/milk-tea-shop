package com.mts.backend.application.store.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import com.mts.backend.shared.command.ICommandHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateServiceTableCommand implements ICommand<CommandResult> {
    private Integer id;
    private String name;
    private Integer areaId;
    private boolean isActive;
}
