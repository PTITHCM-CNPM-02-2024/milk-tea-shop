package com.mts.backend.application.staff.command;

import com.mts.backend.domain.staff.identifier.EmployeeId;
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
public class DeleteEmpByIdCommand implements ICommand<CommandResult> {
    private EmployeeId id;
}
