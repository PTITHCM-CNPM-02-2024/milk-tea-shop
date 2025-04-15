package com.mts.backend.application.store.handler;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mts.backend.application.store.command.DeleteAreaByIdCommand;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;

@Service
public class DeleteAreaByIdCommandHandler implements ICommandHandler<DeleteAreaByIdCommand, CommandResult> {
 private final JpaAreaRepository jpaAreaRepository;


 public DeleteAreaByIdCommandHandler(JpaAreaRepository jpaAreaRepository) {
    this.jpaAreaRepository = jpaAreaRepository;
 }

 @Override
 public CommandResult handle(DeleteAreaByIdCommand command) {
    Objects.requireNonNull(command, "AreaId is required");

    var area = jpaAreaRepository.findById(command.getAreaId().getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy khu vực"));

    jpaAreaRepository.delete(area);

    return CommandResult.success(area.getId());
 }
}