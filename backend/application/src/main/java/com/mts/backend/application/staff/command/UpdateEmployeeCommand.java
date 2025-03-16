package com.mts.backend.application.staff.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class UpdateEmployeeCommand extends UserBaseCommand implements ICommand<CommandResult> {
    private Long id;
    private String position;
}
