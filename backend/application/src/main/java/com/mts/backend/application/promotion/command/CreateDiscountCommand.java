package com.mts.backend.application.promotion.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class CreateDiscountCommand extends DiscountCommand implements ICommand<CommandResult> {

}
