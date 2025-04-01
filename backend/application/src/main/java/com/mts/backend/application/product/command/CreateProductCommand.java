package com.mts.backend.application.product.command;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CreateProductCommand implements ICommand<CommandResult> {
    private ProductName name;
    private  String description;
    private CategoryId categoryId;
    private  Boolean available;
    private  Boolean signature;
    private  String imagePath;
    @Builder.Default
    private List<ProductPriceCommand> productPrices = new ArrayList<>();

    public Optional<CategoryId> getCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public Optional<String> getImagePath() {
        return Optional.ofNullable(imagePath);
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public Optional<Boolean> getAvailable() {
        return Optional.ofNullable(available);
    }
    
    public Optional<Boolean> getSignature() {
        return Optional.ofNullable(signature);
    }
    
    
    
}
