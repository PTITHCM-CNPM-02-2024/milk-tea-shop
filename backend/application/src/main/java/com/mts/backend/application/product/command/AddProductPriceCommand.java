package com.mts.backend.application.product.command;

import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductPriceCommand implements ICommand<CommandResult> {
    ProductId productId;
    @Builder.Default
    List<ProductPriceCommand> productPrices = new ArrayList<>();
}
