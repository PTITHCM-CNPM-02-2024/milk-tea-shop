package com.mts.backend.application.product.handler;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mts.backend.application.product.command.DeleteUnitByIdCommand;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
@Service
public class DeleteUnitByIdCommandHandler implements ICommandHandler<DeleteUnitByIdCommand, CommandResult> {
    private final JpaUnitOfMeasureRepository unitOfMeasureRepository;

    public DeleteUnitByIdCommandHandler(JpaUnitOfMeasureRepository unitOfMeasureRepository) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public CommandResult handle(DeleteUnitByIdCommand command) {
        Objects.requireNonNull(command, "DeleteUnitByIdCommand is null");
        var unitOfMeasure = unitOfMeasureRepository.findById(command.getId().getValue())
                .orElseThrow(() -> new NotFoundException("Đơn vị không tồn tại"));
        unitOfMeasureRepository.delete(unitOfMeasure);
        return CommandResult.success();
    }
}
