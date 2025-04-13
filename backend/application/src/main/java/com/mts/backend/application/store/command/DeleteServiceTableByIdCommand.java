package com.mts.backend.application.store.command;

import org.springframework.stereotype.Service;

import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import com.mts.backend.shared.command.ICommandHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DeleteServiceTableByIdCommand implements ICommand<CommandResult> {
    private ServiceTableId serviceTableId;
}
