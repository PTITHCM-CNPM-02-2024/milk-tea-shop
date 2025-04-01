package com.mts.backend.application.product.command;

import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.domain.product.value_object.QuantityOfProductSize;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductSizeCommand implements ICommand<CommandResult> {
    private ProductSizeId id;
    private UnitOfMeasureId unitId;
    private ProductSizeName name;
    private String description;
    private QuantityOfProductSize quantity;
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
