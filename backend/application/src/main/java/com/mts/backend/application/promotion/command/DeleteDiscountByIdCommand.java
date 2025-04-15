package com.mts.backend.application.promotion.command;

import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.shared.command.ICommand;
import com.mts.backend.shared.command.CommandResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteDiscountByIdCommand implements ICommand<CommandResult> {
    private DiscountId discountId;
}

