package com.mts.backend.application.staff.query;

import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CheckoutTableByEmpIdQuery implements IQuery<CommandResult> {
    private EmployeeId employeeId;
    private Integer size;
    private Integer page;
}
