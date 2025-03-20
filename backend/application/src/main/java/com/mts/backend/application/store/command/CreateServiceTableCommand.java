package com.mts.backend.application.store.command;

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
    
    private Integer areaId;
    private String name;
    
    public Optional<Integer> getAreaId() {
        return Optional.ofNullable(areaId);
    }

}
