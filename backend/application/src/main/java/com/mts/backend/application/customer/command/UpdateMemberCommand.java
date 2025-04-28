package com.mts.backend.application.customer.command;

import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
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
    private MembershipTypeId memberId;
    private MemberTypeName name;
    private String description;
    private DiscountUnit discountUnit;
    private BigDecimal discountValue;
    private int requiredPoint;
    private LocalDateTime validUntil;
    private boolean active;
}
