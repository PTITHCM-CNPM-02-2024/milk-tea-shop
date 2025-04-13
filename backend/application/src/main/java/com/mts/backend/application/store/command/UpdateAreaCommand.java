package com.mts.backend.application.store.command;

import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateAreaCommand implements ICommand<CommandResult> {
    private AreaId areaId;
    private AreaName name;
    private String description;
    private Boolean active;
    private MaxTable maxTable;
    
    public Optional<MaxTable> getMaxTable() {
        return Optional.ofNullable(maxTable);
    }
}
