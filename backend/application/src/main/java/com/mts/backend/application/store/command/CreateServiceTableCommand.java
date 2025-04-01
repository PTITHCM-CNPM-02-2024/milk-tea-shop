package com.mts.backend.application.store.command;

import com.mts.backend.domain.store.identifier.AreaId;
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
@Builder
@Data
public class CreateServiceTableCommand implements ICommand<CommandResult> {
    
    private AreaId areaId;
    private TableNumber name;
    private String description;
    
    public Optional<AreaId> getAreaId(){
        return Optional.ofNullable(areaId);
    }
    
}
