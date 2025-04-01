package com.mts.backend.application.staff.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
@SuperBuilder
public class CreateManagerCommand extends  UserBaseCommand implements ICommand<CommandResult> {
}
