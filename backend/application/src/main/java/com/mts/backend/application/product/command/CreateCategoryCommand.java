package com.mts.backend.application.product.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class CreateCategoryCommand implements ICommand<CommandResult>{
    private String name;
    private String description;
    private Integer parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Optional<Integer> getParentId() {
        return Optional.ofNullable(parentId);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt == null ? LocalDateTime.now() : createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt == null ? LocalDateTime.now() : updatedAt;
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
}
