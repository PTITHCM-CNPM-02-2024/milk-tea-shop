package com.mts.backend.application.staff.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
@Builder
public class CreateManagerCommand extends  UserBaseCommand implements ICommand<CommandResult> {
}
