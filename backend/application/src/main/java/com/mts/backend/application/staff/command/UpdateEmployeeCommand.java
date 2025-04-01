package com.mts.backend.application.staff.command;

import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.value_object.Position;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
public class UpdateEmployeeCommand extends UserBaseCommand implements ICommand<CommandResult> {
    private EmployeeId id;
    private Position position;
}
