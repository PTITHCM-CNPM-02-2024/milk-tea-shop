package com.mts.backend.application.product.command;

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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateProductSizeCommand implements ICommand<CommandResult> {
    private ProductSizeName name;
    private UnitOfMeasureId unitId;
    private QuantityOfProductSize quantity;
    private String description;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
