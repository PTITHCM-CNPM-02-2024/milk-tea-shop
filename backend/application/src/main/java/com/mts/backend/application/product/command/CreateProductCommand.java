package com.mts.backend.application.product.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CreateProductCommand implements ICommand<CommandResult> {
    private  String name;
    private  String description;
    private  Integer categoryId;
    private  Boolean available;
    private  Boolean signature;
    private  String imagePath;
    @Builder.Default
    private Set<ProductPriceCommand> productPrices  = new HashSet<>();

    public Optional<Integer> getCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public Optional<String> getImagePath() {
        return Optional.ofNullable(imagePath);
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    
    
}
