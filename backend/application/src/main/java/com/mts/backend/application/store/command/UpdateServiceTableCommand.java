package com.mts.backend.application.store.command;

import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.value_object.TableNumber;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateServiceTableCommand implements ICommand<CommandResult> {
    private ServiceTableId id;
    private TableNumber name;
    private AreaId areaId;
    private boolean isActive;
    
    public Optional<AreaId> getAreaId() {
        return Optional.ofNullable(areaId);
    }
}
