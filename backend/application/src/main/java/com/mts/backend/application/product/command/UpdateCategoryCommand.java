package com.mts.backend.application.product.command;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;
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
public class UpdateCategoryCommand implements ICommand<CommandResult> {
    private CategoryId id;
    private CategoryName name;
    private String description;
    private CategoryId parentId;
    
    public Optional<CategoryId> getParentId() {
        return Optional.ofNullable(parentId);
    }
}
