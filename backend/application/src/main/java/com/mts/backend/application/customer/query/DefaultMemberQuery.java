package com.mts.backend.application.customer.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class DefaultMemberQuery implements IQuery<CommandResult> {
    private Integer page;
    private Integer size;
}
