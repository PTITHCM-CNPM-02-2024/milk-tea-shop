package com.mts.backend.application.customer.command;

import com.google.auto.value.AutoValue.Builder;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DeleteCusByIdCommand implements ICommand<CommandResult> {

    private CustomerId customerId;
    
}
