package com.mts.backend.application.customer.command;

import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import com.mts.backend.shared.command.CommandResult;

import lombok.Data;

import com.mts.backend.domain.customer.identifier.MembershipTypeId;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DeleteMemberByIdCommand implements ICommand<CommandResult> {

    private MembershipTypeId membershipTypeId;
}
