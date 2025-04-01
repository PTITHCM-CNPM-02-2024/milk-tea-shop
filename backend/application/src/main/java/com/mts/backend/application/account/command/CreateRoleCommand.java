package com.mts.backend.application.account.command;

import com.mts.backend.domain.account.value_object.RoleName;
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
public class CreateRoleCommand implements ICommand<CommandResult> {
    
    private RoleName name;
    private String description;
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
