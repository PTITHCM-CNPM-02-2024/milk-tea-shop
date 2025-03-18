package com.mts.backend.application.store.response;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AreaDetailResponse implements IQuery<CommandResult> {
    private Integer id;
    private String name;
    private boolean isActive;
    private Integer maxTable;
    private String description;
}
