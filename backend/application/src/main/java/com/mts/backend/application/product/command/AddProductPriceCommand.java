package com.mts.backend.application.product.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductPriceCommand implements ICommand<CommandResult> {
    Integer productId;
    @Builder.Default
    Set<ProductPriceCommand> productPrices = new HashSet<>();
}
