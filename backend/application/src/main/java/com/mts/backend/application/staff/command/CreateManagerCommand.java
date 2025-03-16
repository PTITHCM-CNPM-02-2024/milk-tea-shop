package com.mts.backend.application.staff.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
@Builder
public class CreateManagerCommand extends  UserBaseCommand implements ICommand<CommandResult> {
}
