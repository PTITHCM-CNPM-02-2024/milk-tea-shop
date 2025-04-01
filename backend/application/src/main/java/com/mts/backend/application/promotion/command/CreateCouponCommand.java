package com.mts.backend.application.promotion.command;

import com.mts.backend.domain.promotion.value_object.CouponCode;
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
public class CreateCouponCommand implements ICommand<CommandResult> {
    private CouponCode coupon;
    private String description;
    
    public Optional<String> getDescription(){
        return Optional.ofNullable(description);
    }
}
