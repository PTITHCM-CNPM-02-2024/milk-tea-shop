package com.mts.backend.application.staff.command;

import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
public class UpdateManagerCommand extends UserBaseCommand implements ICommand<CommandResult> {
    private ManagerId id;
}
