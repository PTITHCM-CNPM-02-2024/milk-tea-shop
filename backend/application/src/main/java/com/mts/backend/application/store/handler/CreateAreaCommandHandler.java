package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.CreateAreaCommand;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateAreaCommandHandler implements ICommandHandler<CreateAreaCommand, CommandResult> {
    private final JpaAreaRepository areaRepository;


    public CreateAreaCommandHandler(JpaAreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(CreateAreaCommand command) {
        Objects.requireNonNull(command, "Create area command is required");

        var area = Area.builder()
                .id(AreaId.create().getValue())
                .name(command.getName())
                .description(command.getDescription())
                .maxTable(command.getMaxTable().orElse(null))
                .active(command.isActive())
                .build();

        try {
            var savedArea = areaRepository.save(area);
            return CommandResult.success(savedArea.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_area_name")) {
                throw new DuplicateException("Tên khu vực đã tồn tại");
            }
        }
        
        return CommandResult.businessFail("Không thể tạo khu vực");
    }

}
