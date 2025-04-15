package com.mts.backend.application.store.handler;

import org.springframework.stereotype.Service;

import com.mts.backend.application.store.command.DeleteServiceTableByIdCommand;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Service
public class DeleteServiceTableByIdCommandHandler implements ICommandHandler<DeleteServiceTableByIdCommand, CommandResult> {
    private final JpaServiceTableRepository jpaServiceTableRepository;

    public DeleteServiceTableByIdCommandHandler(JpaServiceTableRepository jpaServiceTableRepository) {
        this.jpaServiceTableRepository = jpaServiceTableRepository;
    }

    @Override
    public CommandResult handle(DeleteServiceTableByIdCommand command) {
        Objects.requireNonNull(command, "ServiceTableId is required");

        var serviceTable = jpaServiceTableRepository.findById(command.getServiceTableId().getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy bàn"));

        jpaServiceTableRepository.delete(serviceTable);

        return CommandResult.success(serviceTable.getId());
    }
}
