package com.mts.backend.application.product.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateProductInformCommand implements ICommand<CommandResult>{
    private Integer productId;
    private String name;
    private String description;
    private Integer categoryId;
    private String imagePath;
    private boolean isAvailable;
    private boolean isSignature;
}
