package com.mts.backend.application.product.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateProductPriceCommand implements ICommand<CommandResult> {
    private Integer productId;
    @Builder.Default
    private Set<ProductPriceCommand> productPrices = new HashSet<>();
}
