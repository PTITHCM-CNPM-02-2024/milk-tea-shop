package com.mts.backend.application.promotion.command;

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
public class UpdateCouponCommand implements ICommand<CommandResult> {
    private Long id;
    private String coupon;
    private String description;
}
