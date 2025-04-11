package com.mts.backend.application.product.command;

import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class CreateCategoryCommand implements ICommand<CommandResult>{
    private CategoryName name;
    private String description;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
}
