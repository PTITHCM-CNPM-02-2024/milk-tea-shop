package com.mts.backend.application.customer.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import com.mts.backend.shared.command.ICommandHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateMemberCommand implements ICommand<CommandResult>{
    private Integer memberId;
    private String name;
    private String description;
    private String discountUnit;
    private BigDecimal discountValue;
    private Integer requiredPoints;
    private LocalDateTime validUntil;
    private boolean isActive;
}
