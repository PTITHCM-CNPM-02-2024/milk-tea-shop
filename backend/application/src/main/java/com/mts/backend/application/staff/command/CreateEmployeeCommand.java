package com.mts.backend.application.staff.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateEmployeeCommand extends UserBaseCommand implements ICommand<CommandResult> {
    private String position;
}
