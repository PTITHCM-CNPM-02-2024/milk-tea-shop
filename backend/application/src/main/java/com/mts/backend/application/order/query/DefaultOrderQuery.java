package com.mts.backend.application.order.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class DefaultOrderQuery implements IQuery<CommandResult> {
    private Integer page;
    private Integer size;
}
