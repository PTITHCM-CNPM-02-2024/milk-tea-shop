package com.mts.backend.application.staff.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultEmployeeQuery implements IQuery<CommandResult> {
    private Integer size;
    private Integer page;
    
}
