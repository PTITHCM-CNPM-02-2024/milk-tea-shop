package com.mts.backend.application.store.command;

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
public class UpdateAreaMaxAndActiveCommand implements ICommand<CommandResult> {
    private Integer areaId;
    private Integer maxTable;
    private boolean isActive;
}
