package com.mts.backend.application.customer.command;

import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateMembershipCommand implements ICommand<CommandResult> {
    private MemberTypeName name;
    private String description;
    private DiscountUnit discountUnit;
    private BigDecimal discountValue;
    private int requiredPoints;
    private LocalDateTime validUntil;
    
    public Optional<LocalDateTime> getValidUntil() {
        return Optional.ofNullable(validUntil);
    }
}
