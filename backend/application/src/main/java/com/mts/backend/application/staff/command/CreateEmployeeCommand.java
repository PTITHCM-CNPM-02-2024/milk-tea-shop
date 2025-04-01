package com.mts.backend.application.staff.command;

import com.mts.backend.domain.staff.value_object.Position;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployeeCommand extends UserBaseCommand implements ICommand<CommandResult> {
    private Position position;
}
