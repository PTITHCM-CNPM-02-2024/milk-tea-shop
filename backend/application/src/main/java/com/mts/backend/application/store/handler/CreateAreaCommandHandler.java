package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.CreateAreaCommand;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateAreaCommandHandler implements ICommandHandler<CreateAreaCommand, CommandResult> {
    private final IAreaRepository areaRepository;
    
    
    public CreateAreaCommandHandler(IAreaRepository areaRepository){
        this.areaRepository = areaRepository;
    }
    
    @Override
    public CommandResult handle(CreateAreaCommand command) {
        Objects.requireNonNull(command, "Create area command is required");
        
        var area = new Area(
                AreaId.create(),
                AreaName.of(command.getName()),
                command.getDescription(),
                command.getMaxTable() != null ? MaxTable.of(command.getMaxTable()) : null,
                command.isActive(),
                LocalDateTime.now());
        
        verifyUniqueName(area.getAreaName());
        
        var savedArea = areaRepository.save(area);
        
        return CommandResult.success(savedArea.getId().getValue());
    }
    
    private void verifyUniqueName(AreaName name){
        Objects.requireNonNull(name, "Area name is required");
        if (areaRepository.existsByName(name)){
            throw new DuplicateException("Tên khu vực đã tồn tại");
        }
    }
}
