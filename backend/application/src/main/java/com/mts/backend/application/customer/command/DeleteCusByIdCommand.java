package com.mts.backend.application.customer.command;

import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;  

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class DeleteCusByIdCommand implements ICommand<CommandResult> {

    private CustomerId customerId;
    
}
