package com.mts.backend.application.customer.command;

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
    private String name;
    private String description;
    private String discountUnit;
    private BigDecimal discountValue;
    private Integer requiredPoints;
    private LocalDateTime validUntil;
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
