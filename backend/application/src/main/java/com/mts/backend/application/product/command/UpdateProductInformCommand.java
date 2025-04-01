package com.mts.backend.application.product.command;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateProductInformCommand implements ICommand<CommandResult>{
    private ProductId productId;
    private ProductName name;
    private String description;
    private CategoryId categoryId;
    private String imagePath;
    private boolean isAvailable;
    private boolean isSignature;
}
